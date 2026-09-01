package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;

public interface VacancyRepository extends JpaRepository<VacancyEntity, Integer> {
}