package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.type.vacancy.VacancyStatus;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends JpaRepository<VacancyEntity, Long> {

    @EntityGraph(attributePaths = {"source", "ownerOrganisation", "category"})
    Page<VacancyEntity> findAll(Specification<VacancyEntity> spec, Pageable pageable);

    @Query("select distinct v.status from VacancyEntity v where v.status is not null order by v.status")
    List<String> findDistinctStatuses();

    Optional<VacancyEntity> findFirstBySourceCodeAndStatus(String sourceCode, VacancyStatus status);


    boolean existsByExternalIdAndSourceCode(String externalId, String sourceCode);


    @EntityGraph(attributePaths = {"source", "ownerOrganisation", "category", "grades"})
    Optional<VacancyEntity> findWithDetailsById(Long id);
}