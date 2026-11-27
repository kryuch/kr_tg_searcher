package ru.kryuch.krtg.searcher.entity.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.OrganisationEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "krrg_employee_profile_projects")
@NoArgsConstructor
public class EmployeeProfileProjectEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private EmployeeProfileEntity profile;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private OrganisationEntity organisation;

    @Column(length = 255)
    private String role;

    @Column(name = "start_at")
    private LocalDate startAt;

    @Column(name = "end_at")
    private LocalDate endAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "krrg_employee_profile_project_skills",
        joinColumns = @JoinColumn(name = "project_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "skill_id", nullable = false)
    )
    private Set<DevelopmentSkillEntity> skills;
}