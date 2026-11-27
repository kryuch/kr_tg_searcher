package ru.kryuch.krtg.searcher.repository.development;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DevelopmentGradeRepository extends JpaRepository<DevelopmentGradeEntity, Long> {

    Optional<DevelopmentGradeEntity> findByCode(String code);

    List<DevelopmentGradeEntity> findByCodeIn(Collection<String> codes);

    List<DevelopmentGradeEntity> findAllByOrderBySortOrderAsc();
}