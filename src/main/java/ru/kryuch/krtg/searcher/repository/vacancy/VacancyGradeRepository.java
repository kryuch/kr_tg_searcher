package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyGradeEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VacancyGradeRepository extends JpaRepository<VacancyGradeEntity, Long> {

    Optional<VacancyGradeEntity> findByCode(String code);

    List<VacancyGradeEntity> findByCodeIn(Collection<String> codes);

    List<VacancyGradeEntity> findAllByOrderBySortOrderAsc();
}