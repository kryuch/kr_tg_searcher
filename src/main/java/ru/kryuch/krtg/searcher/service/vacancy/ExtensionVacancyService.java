package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.repository.ExtensionTaskRepository;
import ru.kryuch.krtg.searcher.repository.VacancyRepository;

@Service
@RequiredArgsConstructor
public class ExtensionVacancyService {

    private final VacancyRepository vacancyRepository;
    private final ExtensionTaskRepository taskRepository;

    @Transactional
    public VacancyQuestionAnswerDto processVacancy(
            VacancyQuestionAnswerRequestDto request
    ) {

      /*  VacancyEntity vacancy = vacancyRepository
                .findByUserIdAndExternalId(
                        UserUtil.getCurrentUser().getId(),
                        request.externalId()
                )
                .orElseGet(() -> {
                    VacancyEntity newVacancy = new VacancyEntity();

                    newVacancy.setExternalId(request.externalId());
              //      newVacancy.set(user);

                    return newVacancy;
                });

        vacancy.setTitle(request.title());
        vacancy.setUrl(request.url());
        vacancy.setCompanyName(request.companyName());
        vacancy.setDescription(request.description());

        vacancy = vacancyRepository.save(vacancy);

        /*
         * Пока алгоритм простой:
         * считаем, что на любую полученную вакансию нужно создать APPLY task.
         *
         * Позже сюда подключим VacancyMatchingService.
         */
 /*       ExtensionTask task = new ExtensionTask();

        task.setType(ExtensionTaskType.APPLY);
        task.setStatus(ExtensionTaskStatus.NEW);
        task.setUser(user);
        task.setVacancy(vacancy);
        task.setCreatedAt(java.time.Instant.now());

        task = taskRepository.save(task);

        return new VacancyCreateResponse(
                vacancy.getId(),
                true,
                task.getId()
        );*/
        return null;
    }
}
