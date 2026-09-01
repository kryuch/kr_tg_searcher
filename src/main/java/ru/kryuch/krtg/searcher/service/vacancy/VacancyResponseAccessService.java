package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyResponseRequestDto;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyResponseMapper;
import ru.kryuch.krtg.searcher.repository.ExtensionTaskRepository;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.VacancyRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingValueRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyOwnerOrganisationRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyResponseRepository;
import ru.kryuch.krtg.searcher.service.AbstractAccessService;
import ru.kryuch.krtg.searcher.type.VacancySource;

import java.util.List;

@Service
public class VacancyResponseAccessService extends AbstractAccessService<Long, VacancyResponseEntity, VacancyResponseDto, VacancyResponseMapper, VacancyResponseRepository> {


    // сервис интеграции работает только с одной площадкой
    private static final VacancySource SOURCE = VacancySource.HH;

    private final VacancyRepository vacancyRepository;
    private final VacancyOwnerOrganisationRepository ownerOrganisationRepository;

    public VacancyResponseAccessService(VacancyResponseRepository vacancyResponseRepository,
                                        VacancyResponseMapper vacancyResponseMapper,
                                        VacancyRepository vacancyRepository,
                                        VacancyOwnerOrganisationRepository ownerOrganisationRepository) {
        super(vacancyResponseRepository, vacancyResponseMapper, "отклики на вакансии");
        this.vacancyRepository = vacancyRepository;
        this.ownerOrganisationRepository = ownerOrganisationRepository;
    }

    @Transactional
    public Long add(VacancyResponseRequestDto dto) {
        Integer userId = getCurrentUserId();
        VacancyEntity vacancy = resolveVacancy(dto);

        if (repository.findByIdAndUserId(vacancy.getId(), userId).isPresent()) {
            throw new BusinessException(
                    "Отклик на эту вакансию уже существует: externalId=" + dto.getExternalId());
        }

        VacancyResponseEntity response = VacancyResponseEntity.builder()
                .vacancy(vacancy)
                .coverLetter(dto.getCoverLetter())
                .questionsSnapshot(dto.getQuestions())
                .build();
        response.setUserId(userId);

        return repository.save(response).getId();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDto> getAll() {
        return mapper.fromEntityList(
                repository.findAllByUserIdWithVacancy(getCurrentUserId()));
    }

    private VacancyEntity resolveVacancy(VacancyResponseRequestDto dto) {
        return vacancyRepository.findByExternalIdAndSource(dto.getExternalId(), SOURCE)
                .orElseGet(() -> createVacancy(dto));
    }

    private VacancyEntity createVacancy(VacancyResponseRequestDto dto) {
        VacancyOwnerOrganisationEntity owner = resolveOwnerOrganisation(dto.getOwner());

        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setExternalId(dto.getExternalId());
        vacancy.setSource(SOURCE);
        vacancy.setUrl(dto.getUrl());
        vacancy.setTitle(dto.getTitle());
        vacancy.setDescription(dto.getDescription());
        vacancy.setOwnerOrganisation(owner);

        return vacancyRepository.save(vacancy);
    }

    private VacancyOwnerOrganisationEntity resolveOwnerOrganisation(String name) {
        return ownerOrganisationRepository.findByName(name)
                .orElseGet(() -> {
                    VacancyOwnerOrganisationEntity organisation = new VacancyOwnerOrganisationEntity();
                    organisation.setName(name);
                    return ownerOrganisationRepository.save(organisation);
                });
    }

    // Унаследованные generic-методы не подходят для этой сущности:
    // VacancyResponseDto — плоский DTO (owner/externalId/url и т.п. лежат в
    // связанных Vacancy/OwnerOrganisation), поэтому mapper.toEntity/mergeToEntity
    // не может корректно собрать VacancyResponseEntity обратно. Явно запрещаем
    // случайный вызов через базовый тип, чтобы не получить NPE на vacancy=null.

    @Override
    public void add(VacancyResponseDto dto) {
        throw new UnsupportedOperationException(
                "Используйте add(VacancyResponseRequestDto) — прямое создание из VacancyResponseDto не поддерживается");
    }

    @Override
    public void update(VacancyResponseDto dto, Long id) {
        throw new UnsupportedOperationException(
                "Обновление отклика через generic DTO не поддерживается");
    }
}