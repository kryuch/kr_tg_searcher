package ru.kryuch.krtg.searcher.entity.development;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;

@Entity
@Table(name = "krrg_grades")
@Getter
@Setter
@NoArgsConstructor
public class DevelopmentGradeEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer level;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}