package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.type.ExtensionTaskStatus;

import java.util.List;

public interface ExtensionTaskRepository
        extends JpaRepository<VacancyOwnerOrganisationEntity, Long> {

    List<VacancyOwnerOrganisationEntity> findByUserIdAndStatusOrderByCreatedAtAsc(
            Long userId,
            ExtensionTaskStatus status
    );
}