package ru.kryuch.krtg.searcher.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDto {

    private Long id;

    private String name;

    public UniversityDto(String name) {
        this.name = name;
    }
}
