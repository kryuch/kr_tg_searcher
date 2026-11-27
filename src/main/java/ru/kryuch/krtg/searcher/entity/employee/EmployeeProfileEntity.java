package ru.kryuch.krtg.searcher.entity.employee;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;

@Getter
@Setter
@Entity
@Table(name = "krrg_employee_profiles")
@NoArgsConstructor
public class EmployeeProfileEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "krrg_employee_profile_grades",
        joinColumns = @JoinColumn(name = "profile_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "grade_id", nullable = false)
    )
    private Set<DevelopmentGradeEntity> grades;

    @Column(name = "rate_per_hour", precision = 12, scale = 2)
    private BigDecimal ratePerHour;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeProfileProjectEntity> projects;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "development_specialization_id")
    private DevelopmentSpecializationEntity developmentSpecialization;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "krrg_employee_profile_skills",
        joinColumns = @JoinColumn(name = "profile_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "skill_id", nullable = false)
    )
    private Set<DevelopmentSkillEntity> skills;

}