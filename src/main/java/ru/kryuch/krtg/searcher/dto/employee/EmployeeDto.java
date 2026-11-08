package ru.kryuch.krtg.searcher.dto.employee;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {
    private String lastName;
    private String firstName;
    private String middleName;
    private String gender;
    private LocalDate birthDate;
}
