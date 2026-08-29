package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;

import java.util.Optional;

public interface VacancyRepository
        extends JpaRepository<VacancyEntity, Long> {

    Optional<VacancyEntity> findByUserIdAndExternalId(
            Long userId,
            String externalId
    );
}