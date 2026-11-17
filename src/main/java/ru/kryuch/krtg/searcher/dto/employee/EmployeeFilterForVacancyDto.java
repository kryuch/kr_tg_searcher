package ru.kryuch.krtg.searcher.dto.employee;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.util.Set;

@Builder
@Getter
@ToString
public class EmployeeFilterForVacancyDto {

    private Set<String> gradeCodes;
    private WorkFormat workFormat;
    private String title;
}
