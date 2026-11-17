package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.GradeEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<GradeEntity, Long> {

    Optional<GradeEntity> findByCode(String code);

    List<GradeEntity> findByCodeIn(Collection<String> codes);

    List<GradeEntity> findAllByOrderBySortOrderAsc();
}