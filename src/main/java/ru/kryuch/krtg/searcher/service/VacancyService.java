package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.VacanciesContainer;
import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.helper.MessagesHelper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.type.VacancyTgOwnerStatus;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final ChatRepository chatRepository;
    private final SettingService settingService;

    private static final String VACANCY_TERM_SETTING = "text_in_vacancy";


    public VacanciesContainer analyze(MessagesHistory messagesHistory) {
        VacanciesContainer vacanciesContainer = new VacanciesContainer();
        vacanciesContainer.setMessagesHistory(messagesHistory);
        Set<String> newTg = new HashSet<>();
        String term = settingService.getValueByCode(VACANCY_TERM_SETTING);

        List<VacancyInfo> vacancyInfoList =
                messagesHistory.getValues().stream()
                        .filter(message -> Objects.nonNull(message))
                        .map(message -> MessagesHelper.createVacancyInfo(message.getValue(), message.getDateTime()))
                        .filter(item -> Objects.nonNull(item) && Objects.nonNull(item.getTitle()))
                        .toList();

        Set<String> tgSet =
                vacancyInfoList.stream()
                        .filter(item -> Objects.nonNull(item.getTg()))
                        .map(item -> UserUtil.normalizeUsername(item.getTg())).collect(Collectors.toSet());

        Set<String> existingTg = chatRepository.findExistingUsername(tgSet);

        vacancyInfoList.forEach(vacancyInfo -> enrich(newTg, vacancyInfo, term, existingTg));

        vacanciesContainer.setVacancies(vacancyInfoList);
        vacanciesContainer.setNewTg(newTg);
        return vacanciesContainer;
    }

    private void enrich(Set<String> newTg, VacancyInfo vacancyInfo, String term, Set<String> existingTg) {
        if (Objects.nonNull(vacancyInfo.getTg())) {
            if (existingTg.contains(UserUtil.normalizeUsername(vacancyInfo.getTg()).toLowerCase())) {
                vacancyInfo.setStatus(VacancyTgOwnerStatus.EXIST);
            } else {
                vacancyInfo.setStatus(VacancyTgOwnerStatus.NEW);
                boolean matches =
                        term == null ||
                                vacancyInfo.getText().contains(term);

                if (matches) {
                    newTg.add(vacancyInfo.getTg());
                }
            }
        }
    }


}
