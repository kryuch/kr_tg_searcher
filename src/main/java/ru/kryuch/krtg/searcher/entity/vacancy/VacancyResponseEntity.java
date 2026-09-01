package ru.kryuch.krtg.searcher.entity.vacancy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;

@Entity
@Table(
        name = "krrg_vacancy_responses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"vacancy_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyResponseEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private VacancyEntity vacancy;

    // JSON-снимок вопросов/ответов на момент отклика (аудит) — не связан с текущим
    // состоянием VacancyQuestionAnswerEntity, может расходиться со временем
    @Column(name = "questions_snapshot", columnDefinition = "text")
    private String questionsSnapshot;

    @Column(name = "cover_letter", columnDefinition = "text")
    private String coverLetter;
}