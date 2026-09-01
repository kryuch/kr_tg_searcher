package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VacancyResponseDto {
    private Integer id;
    private Integer externalId;
    private String url;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String coverLetter;
    private String owner;
}
