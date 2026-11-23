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
import lombok.Getter;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;
import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;
import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;

@Entity
@Table(name = "krrg_employee_educations")
@Getter
@Setter
public class EmployeeEducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private EducationUniversityEntity university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_type_id", nullable = false)
    private EducationTypeEntity educationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private EducationSpecialtyEntity specialty;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;
}
