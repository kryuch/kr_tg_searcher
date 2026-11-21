package ru.kryuch.krtg.searcher.entity.employee;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;

@Entity
@Table(
    name = "krrg_employee_languages",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_employee_languages__employee_language",
        columnNames = {"employee_id", "language_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class EmployeeLanguageEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private LocationLanguageEntity language;

    @Column(name = "level", length = 50)
    private String level;
}