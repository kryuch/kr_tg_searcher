package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Data;

@Data
public class VacancyResponseRequest {

    private Integer id;
    private String url;
    private String owner;
    private String title;
    private String description;
    private String coverLetter;
}