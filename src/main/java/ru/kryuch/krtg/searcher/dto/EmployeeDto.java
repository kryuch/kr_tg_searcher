package ru.kryuch.krtg.searcher.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeContactDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeLanguageDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String gender;
    private boolean isMale;
    private LocalDate birthDate;
    private Long citizenshipId;
    private Long cityId;
    private Long stateId;
    private List <EmployeeContactDto> contacts;
    private List <EmployeeLanguageDto> languages;
    private List <EmployeeProfileDto> profiles;
    private List <EmployeeEducationDto> educations;
    public String getName() {
        return lastName + " " + firstName + " " + middleName;
    }
}
