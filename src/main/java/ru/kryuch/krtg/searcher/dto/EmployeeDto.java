package ru.kryuch.krtg.searcher.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguage;

@Getter
@Setter
@Builder
public class EmployeeDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String gender;
    private boolean isMale;
    private LocalDate birthDate;
    private String citizenship;
    private String city;
    private String state;
    private List <EmployeeContactDto> contacts;
    private List <EmployeeLanguage> languages;

    public String getName() {
        return lastName + " " + firstName + " " + middleName;
    }
}
