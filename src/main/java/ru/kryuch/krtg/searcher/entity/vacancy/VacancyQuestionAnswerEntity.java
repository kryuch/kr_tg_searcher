package ru.kryuch.krtg.searcher.entity.vacancy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "krrg_vacancy_question_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyQuestionAnswerEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private VacancyQuestionEntity question;

    @Column(name = "text_value", length = 4000)
    private String textValue;

    @Column(name = "bool_value")
    private Boolean boolValue;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VacancyQuestionAnswerOptionEntity> selectedOptions = new ArrayList<>();
}