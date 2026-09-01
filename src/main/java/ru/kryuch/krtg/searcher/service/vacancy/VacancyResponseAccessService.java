package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseDto;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyResponseMapper;
import ru.kryuch.krtg.searcher.repository.ExtensionTaskRepository;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.VacancyRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingValueRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyResponseRepository;
import ru.kryuch.krtg.searcher.service.AbstractAccessService;

@Service
public class VacancyResponseAccessService extends AbstractAccessService<Long, VacancyResponseEntity, VacancyResponseDto, VacancyResponseMapper, VacancyResponseRepository> {

    public VacancyResponseAccessService(VacancyResponseRepository vacancyResponseRepository, VacancyResponseMapper vacancyResponseMapper) {
        super(vacancyResponseRepository, vacancyResponseMapper, "отклики на вакансии");
    }

    public void add(VacancyResponseDto vacancyResponseDto) {

    }
}
