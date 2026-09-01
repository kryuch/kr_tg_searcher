package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;

public interface VacancyResponseRepository extends JpaRepository<VacancyResponseEntity, Long> {

}