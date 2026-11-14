package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyCategoryEntity;

import java.util.Optional;

public interface VacancyCategoryRepository extends JpaRepository<VacancyCategoryEntity, Long> {

    Optional<VacancyCategoryEntity> findByName(String name);
}