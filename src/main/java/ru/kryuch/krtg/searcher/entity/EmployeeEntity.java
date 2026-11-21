package ru.kryuch.krtg.searcher.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeContactEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeLanguageEntity;
import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;

@Getter
@Setter
@Entity
@Table(name = "krrg_employees")
public class EmployeeEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", nullable = false, length = 100)
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "is_male")
    private Boolean isMale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizenship_id")
    private LocationStateEntity citizenship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private LocationCityEntity city;

    @OneToMany(
        mappedBy = "employee",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileEntity> employeeProfiles;

    @OneToMany(
        mappedBy = "employee",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<EmployeeLanguageEntity> languages = new HashSet<>();

    @OneToMany(
        mappedBy = "employee",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<EmployeeContactEntity> contacts = new HashSet<>();

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}