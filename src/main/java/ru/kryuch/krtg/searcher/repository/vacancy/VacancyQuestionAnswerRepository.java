package ru.kryuch.krtg.searcher.repository.vacancy;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VacancyQuestionAnswerRepository extends BaseAccessRepository<VacancyQuestionAnswerEntity, Integer> {

    Optional<VacancyQuestionAnswerEntity> findByQuestionIdAndUserId(Integer questionId, Integer userId);

    @Query("""
            SELECT DISTINCT a FROM VacancyQuestionAnswerEntity a
            LEFT JOIN FETCH a.selectedOptions so
            LEFT JOIN FETCH so.option
            WHERE a.userId = :userId
            """)
    List<VacancyQuestionAnswerEntity> findAllByUserIdWithOptions(@Param("userId") Integer userId);

    @Query("""
            SELECT DISTINCT a FROM VacancyQuestionAnswerEntity a
            LEFT JOIN FETCH a.selectedOptions so
            LEFT JOIN FETCH so.option
            WHERE a.userId = :userId AND a.question.id IN :questionIds
            """)
    List<VacancyQuestionAnswerEntity> findAllByUserIdAndQuestionIdIn(
            @Param("userId") Integer userId,
            @Param("questionIds") Collection<Integer> questionIds);
}

