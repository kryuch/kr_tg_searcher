package ru.kryuch.krtg.searcher.integration.hh.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VacancyAnalysisResponse {

    private boolean suitable;

    private String reason;

    private Long vacancyId;

    private String title;

    private List<QuestionsResponse> questions;
}