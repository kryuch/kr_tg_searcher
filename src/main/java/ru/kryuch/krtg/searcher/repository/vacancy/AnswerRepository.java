package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;

public interface AnswerRepository
        extends JpaRepository<VacancyQuestionAnswerEntity, Long> {
}