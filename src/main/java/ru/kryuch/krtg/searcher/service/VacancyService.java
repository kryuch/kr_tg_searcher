package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.MessagesHistory;
import ru.kryuch.krtg.searcher.dto.VacanciesContainer;
import ru.kryuch.krtg.searcher.dto.VacancyInfo;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestDto;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.helper.ChatAccessHelper;
import ru.kryuch.krtg.searcher.helper.MessagesHelper;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancySkillstafRequestMapper;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;
import ru.kryuch.krtg.searcher.repository.VacancyRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancySourceRepository;
import ru.kryuch.krtg.searcher.type.vacancy.VacancySource;
import ru.kryuch.krtg.searcher.type.vacancy.VacancyTgOwnerStatus;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacancyService {
    private final SettingService settingService;
    private final IgnoreRepository ignoreRepository;
    private final ChatAccessHelper chatAccessHelper;

    private static final String VACANCY_TERM_SETTING = "text_in_vacancy";

    private final VacancyRepository vacancyRepository;
    private final VacancySourceRepository vacancySourceRepository;
    private final VacancySkillstafRequestMapper vacancySkillstafRequestMapper;

    @Transactional
    public Long add(VacancySkillstaffRequestDto dto) {
        log.info("VacancyService::add ({})", dto);
        VacancyEntity vacancy = vacancySkillstafRequestMapper.toEntity(dto);

        if (vacancyRepository.existsByExternalIdAndSourceCode(vacancy.getExternalId(), VacancySource.SKILLSTAFF.getCode())) {
            throw new IllegalStateException(
                "Вакансия уже добавлена, externalId=" + vacancy.getExternalId());
        }

        return vacancyRepository.save(vacancy).getId();
    }

    @Transactional
    public boolean exists(String externalId) {
        return vacancySourceRepository.findByCode(VacancySource.SKILLSTAFF.getCode())
            .map(source -> vacancyRepository.existsByExternalIdAndSourceCode(externalId, source.getCode()))
            .orElse(false);
    }

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
                        .map(item -> UserUtil.normalizeUsername(item.getTg()).toLowerCase())
                        .collect(Collectors.toSet());

        Set<String> existingTg = chatAccessHelper.findUniqUsername(tgSet, false);

        vacancyInfoList.forEach(vacancyInfo -> enrich(newTg, vacancyInfo, term, existingTg));

        vacanciesContainer.setVacancies(vacancyInfoList);
        vacanciesContainer.setNewTg(
                ignoreRepository.findNonExistingUsernames(
                        UserUtil.normalizeUsernames(newTg)
                ).stream().collect(Collectors.toSet())
        );
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
