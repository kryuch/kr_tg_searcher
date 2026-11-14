package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancySourceEntity;

import java.util.Optional;

public interface VacancySourceRepository extends JpaRepository<VacancySourceEntity, Long> {

    Optional<VacancySourceEntity> findByCode(String code);
}