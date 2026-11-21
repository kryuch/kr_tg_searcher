package ru.kryuch.krtg.searcher.entity.employee;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

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
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.GradeEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;

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
    private Set<GradeEntity> grades;

    @Column(name = "rate_per_hour", precision = 12, scale = 2)
    private BigDecimal ratePerHour;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeProfileProjectEntity> projects;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileDirectionEntity> profileDirections;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<EmployeeProfileSkillEntity> profileSkills;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileOrganizationEntity> profileOrganizations;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileUniversityEntity> profileUniversities;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileEducationTypeEntity> profileEducationTypes;

    public String getEmployeeFio() {
        return String.format("%s %s %s",
            employee.getLastName(),
            employee.getFirstName(),
            employee.getMiddleName()
        );
    }

    public String getEducationTypeNames() {
        return profileEducationTypes.stream()
            .map(pet -> pet.getEducationType().getName())
            .collect(Collectors.joining(", "));
    }

    public String getUniversityNames() {
        return profileUniversities.stream()
            .map(pn -> pn.getUniversity().getName())
            .collect(Collectors.joining(", "));
    }

    public String getOrganizationNames() {
        return null; // TODO: реализовать, когда будет связь
    }

    public String getSkillNames() {
        return profileSkills.stream()
            .map(ps -> ps.getSkill().getName())
            .collect(Collectors.joining(", "));
    }

    public String getDirectionNames() {
        return profileDirections.stream()
            .map(pd -> pd.getDirection().getName())
            .collect(Collectors.joining(", "));
    }

    public String getGradeNames() {
        if (grades == null || grades.isEmpty()) {
            return "";
        }
        return grades.stream()
            .map(GradeEntity::getName)
            .collect(Collectors.joining(", "));
    }
}