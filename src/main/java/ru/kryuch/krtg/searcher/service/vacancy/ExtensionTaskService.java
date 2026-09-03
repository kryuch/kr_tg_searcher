package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyResponseMapper;
import ru.kryuch.krtg.searcher.dto.vacancy.ExtensionTaskResponse;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.repository.E;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtensionTaskService {

  //  private final E taskRepository;

    @Transactional(readOnly = true)
    public List<ExtensionTaskResponse> getNewTasks() {

        return null;/*taskRepository
                .findByUserIdAndStatusOrderByCreatedAtAsc(
                        UserUtil.getCurrentUser().getId(),
                        ExtensionTaskStatus.NEW
                )
                .stream()
                .map(this::toResponse)
                .toList();*/
    }

    @Transactional
    public void completeTask(
            Long taskId,
            VacancyResponseMapper request
    ) {

       /* VacancyOwnerOrganisationEntity task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Extension task not found: " + taskId
                        )
                );
*/
        /*
         * Очень важно:
         * extension не должен иметь возможность завершить
         * чужую задачу.
         */
       /* if (!task.getUser().getId().equals(UserUtil.getCurrentUser().getId())) {
            throw new IllegalArgumentException(
                    "Task does not belong to current user"
            );
        }

        task.setStatus(
                request.success()
                        ? ExtensionTaskStatus.COMPLETED
                        : ExtensionTaskStatus.FAILED
        );

        task.setErrorMessage(request.errorMessage());
        task.setCompletedAt(Instant.now());
*/
      //  taskRepository.save(task);
    }

    private ExtensionTaskResponse toResponse(VacancyOwnerOrganisationEntity task) {
return null;/*
        VacancyEntity vacancy = task.getVacancy();

        return new ExtensionTaskResponse(
                task.getId(),
                task.getType(),
                task.getStatus(),
                vacancy.getId(),
                vacancy.getExternalId(),
                vacancy.getTitle(),
                vacancy.getUrl()
        );*/
    }

}
