package ru.kryuch.krtg.searcher.dto;

import lombok.Value;

@Value
public class GradeDto {

    private final String code;
    private final String name;
    private final Integer level;
}
