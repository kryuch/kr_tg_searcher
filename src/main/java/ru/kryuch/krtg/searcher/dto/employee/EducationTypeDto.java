package ru.kryuch.krtg.searcher.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationTypeDto {

    private Long id;

    private String name;

    public EducationTypeDto(String name) {
        this.name = name;
    }
}
