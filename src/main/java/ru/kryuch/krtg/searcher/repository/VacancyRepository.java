package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.type.vacancy.VacancyStatus;

import java.util.Optional;

public interface VacancyRepository extends JpaRepository<VacancyEntity, Long> {

    boolean existsByExternalIdAndSourceCode(String externalId, String sourceCode);

    Optional<VacancyEntity> findFirstBySourceCodeAndStatus(String sourceCode, VacancyStatus status);

}