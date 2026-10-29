package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

@Data
public class VacancyRequest {

    private Integer externalId;;
    private String url;
    private String title;
    private String description;
}
