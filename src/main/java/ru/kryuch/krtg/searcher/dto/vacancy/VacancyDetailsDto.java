package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VacancyDetailsDto extends VacancyListItemDto {

    private String location;
    private String citizenship;
    private Integer availabilityUtcOffset;
    private Integer durationMonths;
    private boolean indefinite;
    private boolean extendable;
    private LocalDate startDate;
    private LocalDate endDate;
    private String durationText;
    private String workloadLevel;
    private Integer workloadHoursPerMonth;
    private String clientPaymentTerms;
    private Integer clientReviewsCount;
    private LocalDate actualOn;
    private String description;
    private String requirements;
    private String additionalRequirements;
    private String selectionStages;
    private String joiningConditions;
    private boolean detailsLoaded;
    private String rawText;
}