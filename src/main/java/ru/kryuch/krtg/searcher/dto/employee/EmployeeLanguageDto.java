package ru.kryuch.krtg.searcher.dto.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeLanguageDto {

    private Long languageId;

    private String level;
}
