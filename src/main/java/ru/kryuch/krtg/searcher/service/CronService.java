package ru.kryuch.krtg.searcher.service;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.entity.UserEntity;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;
import ru.kryuch.krtg.searcher.repository.UserRepository;
import ru.kryuch.krtg.searcher.type.PersonalChatType;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

    private final SettingAccessService settingAccessService;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final TelegramMessagingService telegramMessagingService;
    private final TgAccountRepository tgAccountRepository;

    private static final String CRONTIME_CODE = "cron_time";
    private static final String CRON_LASTRUN_CODE = "cron_lastrun";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedDelay = 120000)
    public void schedule() {


        settingAccessService.findAllCronEabled().forEach((userId, value) -> {
            try {
                if (value.equals("0")) {
                    return;
                }

                String cronTab = settingAccessService.getValueByCode(CRONTIME_CODE, userId);
                String cronLastRun = settingAccessService.getValueByCode(CRON_LASTRUN_CODE, userId);

                if (shouldRun(cronTab, cronLastRun)) {
                    SecurityContextHolder.getContext().setAuthentication(
                            createAuthentication(userId)
                    );

                    log.info("Запуск задачи по расписанию: {}", cronTab);
                    doTask(userId);
                    String now = LocalDateTime.now().format(FORMATTER);
                    settingAccessService.setValueByCode(CRON_LASTRUN_CODE, now, userId);
                    log.info("Задача выполнена, обновлено время: {}", now);
                }
            }
            catch (Exception ex) {
                log.error(String.valueOf(ex.getStackTrace()));
            }
        });
    }

    private Authentication createAuthentication(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + userId));

        CurrentUser currentUser = CurrentUser.builder()
                .id(user.getId())
                .username(user.getLogin())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                currentUser.getAuthorities()
        );
    }

    private boolean shouldRun(String cronTab, String cronLastRun) {
        if (cronTab == null || cronTab.isEmpty()) {
            return false;
        }

        try {
            LocalDateTime nextExecution = getNextExecutionTime(cronTab);
            LocalDateTime now = LocalDateTime.now();

            // Если задача никогда не запускалась — запускаем сейчас
            if (cronLastRun == null || cronLastRun.isEmpty()) {
                log.info("🟢 Первый запуск задачи по расписанию: {}", cronTab);
                return true;
            }

            LocalDateTime lastRun = LocalDateTime.parse(cronLastRun, FORMATTER);

            // Проверяем: следующее выполнение уже наступило И последний запуск был до него
            if (!nextExecution.isAfter(now) && lastRun.isBefore(nextExecution)) {
                log.info("🟢 Запуск задачи по расписанию: {}, последний запуск: {}", cronTab, lastRun);
                return true;
            }

            log.debug("⏳ Ожидание следующего выполнения: {}", nextExecution);
            return false;

        } catch (Exception e) {
            log.error("Ошибка при проверке расписания", e);
            return false;
        }
    }

    private LocalDateTime getNextExecutionTime(String cronTab) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
        Cron cron = parser.parse(cronTab);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Optional<ZonedDateTime> next = executionTime.nextExecution(now);

        if (next.isPresent()) {
            return next.get().toLocalDateTime();
        } else {
            throw new IllegalStateException("Невозможно вычислить следующее время выполнения для крона: " + cronTab);
        }
    }

    private void doTask(Integer userId) {
        log.info("Выполнение задачи по расписанию");
        SearchParams searchParams =
                SearchParams.builder()
                        .groupType(PersonalChatType.PERSONAL)
                        .minDiffDaysCount(Integer.valueOf(settingAccessService.getValueByCode("max_day", userId)))
                        .botType(PersonalChatType.PERSONAL)
                        .excludeStatusFlag(true)
                        .term(settingAccessService.getValueByCode("term", userId))
                        .lastMessage(settingAccessService.getValueByCode("cron_lastmessage", userId))
                        .maxFoundCount(16)
                        .messagesCount(0)
                        .tgAccountIds(tgAccountRepository.getAllIds())
                        .build();

        List<ChatInfo> chats = chatService.search(searchParams, false);

        log.info("Найдены " + chats.stream().map(ChatInfo::getName).collect(Collectors.joining(", ")));

        chats = telegramMessagingService.sendToChats(
                settingAccessService.getValueByCode("cron_newmessage", userId),
                chats.stream().map(ChatInfo::getId).toList()
        );

        log.info("Сообщение отправлено в " + chats.stream().map(ChatInfo::getName).collect(Collectors.joining(", ")));
    }
}