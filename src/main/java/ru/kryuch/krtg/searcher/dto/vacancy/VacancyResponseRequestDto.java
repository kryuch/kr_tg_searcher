package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;

@Data
public class VacancyResponseRequestDto {

    private String externalId;
    private String url;
    private String owner;
    private String title;
    private String description;
    private String coverLetter;
    private String questions;
}