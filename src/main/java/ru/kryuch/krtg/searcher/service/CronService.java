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
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.entity.UserEntity;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.integration.dto.ChatResponse;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;
import ru.kryuch.krtg.searcher.repository.UserRepository;
import ru.kryuch.krtg.searcher.type.PersonalChatType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

    private final SettingAccessService settingAccessService;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final TelegramMessagingService telegramMessagingService;
    private final TgAccountRepository tgAccountRepository;
    private final ChatHelper chatHelper;


    private static final long CRON_DELAY = 120_000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));

    @Scheduled(fixedDelay = CRON_DELAY)
    public void schedule() {


        settingAccessService.findAllCronEnabled().forEach((userId, value) -> {
            try {
                if ("0".equals(value)) {
                    return;
                }

                String cronTab = settingAccessService.getValueByCode(SettingConfig.CRONTIME_SETTING_CODE, userId);
                String cronLastRun = settingAccessService.getValueByCode(SettingConfig.CRON_LASTRUN_SETTING_CODE, userId);

                if (shouldRun(cronTab, cronLastRun)) {
                    SecurityContextHolder.getContext().setAuthentication(
                            createAuthentication(userId)
                    );

                    log.info("Запуск задачи по расписанию: {}", cronTab);
                    doTask(userId);
                    String now = LocalDateTime.now().format(FORMATTER);
                    settingAccessService.setValueByCode(SettingConfig.CRON_LASTRUN_SETTING_CODE, now, userId);
                    log.info("Задача выполнена, обновлено время: {}", now);
                }
            }
            catch (Exception ex) {
                log.error("Ошибка выполнения cron для пользователя {}", userId, ex);
            }
            finally {
                SecurityContextHolder.clearContext();
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

            if (cronLastRun == null || cronLastRun.isEmpty()) {
                log.info("🟢 Первый запуск задачи по расписанию: {}", cronTab);
                return true;
            }

            LocalDateTime lastRun = LocalDateTime.parse(cronLastRun, FORMATTER);

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
                        .minDiffDaysCount(Integer.valueOf(settingAccessService.getValueByCode(SettingConfig.MAX_DAY_SETTING_CODE, userId)))
                        .botType(PersonalChatType.PERSONAL)
                        .excludeStatusFlag(true)
                        .term(settingAccessService.getValueByCode(SettingConfig.TERM_SETTING_CODE, userId))
                        .lastMessage(settingAccessService.getValueByCode(SettingConfig.CRON_LASTMESSAGE_SETTING_CODE, userId))
                        .maxFoundCount(Integer.valueOf(settingAccessService.getValueByCode(SettingConfig.CRON_CHATS_COUNT, userId)))
                        .messagesCount(0)
                        .tgAccountIds(tgAccountRepository.getAllIds())
                        .build();

        List<ChatInfo> chats = chatService.search(searchParams, false);

        log.info(
                "Найдено {} чатов: {}",
                chats.size(),
                chats.stream()
                        .map(item -> item.getUsername() + "(" + item.getId()+")")
                        .collect(Collectors.joining(", "))
        );

        List <ChatResponse> chatResponses = telegramMessagingService.sendToChats(
                settingAccessService.getValueByCode(SettingConfig.CRON_NEWMESSAGE_SETTING_CODE, userId),
                false,
                chatHelper.getChatIdsByChatInfo(chats)
        );

        log.info("Сообщение отправлено в " + chatResponses.stream().map(ChatResponse::getName).collect(Collectors.joining(", ")));
    }
}