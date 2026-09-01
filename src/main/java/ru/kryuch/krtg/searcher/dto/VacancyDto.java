package ru.kryuch.krtg.searcher.dto;

import lombok.Value;

@Value
public class VacancyDto {

    private Integer id;
    private Integer externalId;
    private String url;
    private String title;
    private String description;
}
