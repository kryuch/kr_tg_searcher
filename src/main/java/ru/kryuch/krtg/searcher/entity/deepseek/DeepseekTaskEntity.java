package ru.kryuch.krtg.searcher.entity.deepseek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;

@Entity
@Table(name = "krrg_deepseek_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeepseekTaskEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String result;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeepseekTaskStatus status;

    @Column(columnDefinition = "text")
    private String error;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private DeepseekTasksGroupEntity group;
}