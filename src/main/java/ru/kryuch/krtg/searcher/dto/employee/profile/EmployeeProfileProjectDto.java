package ru.kryuch.krtg.searcher.dto.employee.profile;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeProfileProjectDto {

    private Long id;
    private String name;
    private String description;
    private String progress;
    private String organisation;
    private String role;
    private LocalDate startAt;
    private LocalDate endAt;
    private Set<String> skills;
}
