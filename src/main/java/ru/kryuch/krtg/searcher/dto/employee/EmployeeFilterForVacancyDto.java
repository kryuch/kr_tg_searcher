package ru.kryuch.krtg.searcher.dto.employee;

import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.util.Set;

public class EmployeeFilterForVacancyDto {

    private Set<String> gradeCodes;
    private WorkFormat workFormat;
}
