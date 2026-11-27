package ru.kryuch.krtg.searcher.dto.developement;

import lombok.Value;

@Value
public class DevelopmentGradeDto {

    private final String code;
    private final String name;
    private final Integer level;
    private final Integer id;
}
