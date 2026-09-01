package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.type.VacancySource;

import java.util.Optional;

public interface VacancyRepository extends JpaRepository<VacancyEntity, Integer> {

    Optional<VacancyEntity> findByExternalIdAndSource(String externalId, VacancySource source);

}