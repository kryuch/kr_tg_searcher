package ru.kryuch.krtg.searcher.entity.vacancy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;

@Entity
@Table(
        name = "krrg_vacancy_question_answer_options",
        uniqueConstraints = @UniqueConstraint(columnNames = {"answer_id", "option_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class VacancyQuestionAnswerOptionEntity extends TimestampedEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private VacancyQuestionAnswerEntity answer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private VacancyQuestionOptionEntity option;
}
