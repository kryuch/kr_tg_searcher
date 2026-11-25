package ru.kryuch.krtg.searcher.dto.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeEducationDto {

    private Long id;
    private Integer startYear;
    private Integer endYear;

    private Long universityId;
    private Long educationTypeId;
    private Long degreeId;
    private Long specialityId;
}
