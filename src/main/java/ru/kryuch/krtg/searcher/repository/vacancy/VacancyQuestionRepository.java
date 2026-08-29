package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;

import java.util.List;
import java.util.Optional;

public interface VacancyQuestionRepository extends JpaRepository<VacancyQuestionEntity, Integer> {

    @Query("SELECT DISTINCT q FROM VacancyQuestionEntity q LEFT JOIN FETCH q.options")
    List<VacancyQuestionEntity> findAllWithOptions();

    @Query("SELECT q FROM VacancyQuestionEntity q LEFT JOIN FETCH q.options WHERE q.id = :id")
    Optional<VacancyQuestionEntity> findByIdWithOptions(@Param("id") Integer id);
}