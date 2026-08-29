package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;

import java.util.Optional;

public interface QuestionRepository extends CrudRepository<VacancyQuestionEntity, Integer> {

    Optional <VacancyQuestionEntity> findByText(String text);
}
