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

@Entity
@Table(name = "krrg_employee_educations")
@Getter
@Setter
public class EmployeeEducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "university", nullable = false, unique = true, length = 100)
    private String university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_type_id")
    private EducationTypeEntity educationType;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private SpecialtyEntity specialty;
}
