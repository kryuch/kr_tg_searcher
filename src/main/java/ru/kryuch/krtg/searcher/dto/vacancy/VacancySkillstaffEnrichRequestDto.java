package ru.kryuch.krtg.searcher.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancySkillstaffEnrichRequestDto {

    // --- идентификация ---
    @Size(max = 255)
    private String externalId;

    @Size(max = 1000)
    private String url;

    @NotBlank
    @Size(max = 1000)
    private String title;

    @Size(max = 50)
    private String status;

    private LocalDate actualOn;

    // --- классификация ---
    @Size(max = 255)
    private String category;

    @Size(max = 500)
    private String industry;

    @NotEmpty
    private Set<@Size(max = 20) String> grades;

    // --- ставка ---
    @PositiveOrZero
    private BigDecimal rateResident;

    @PositiveOrZero
    private BigDecimal rateNonResident;

    @Size(max = 3)
    private String rateCurrency;

    @Size(max = 20)
    private String rateUnit;

    private Boolean rateOpen;

    // --- условия ---
    @Positive
    private Integer specialistsCount;

    private WorkFormat workFormat;

    @Size(max = 1000)
    private String location;

    @Size(max = 255)
    private String citizenship;

    @Min(-12)
    @Max(14)
    private Integer availabilityUtcOffset;

    // --- длительность и нагрузка ---
    @Positive
    private Integer durationMonths;

    private boolean indefinite;

    private boolean extendable;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 500)
    private String durationText;

    @Size(max = 50)
    private String workloadLevel;

    @Positive
    private Integer workloadHoursPerMonth;

    // --- клиент ---
    private String clientPaymentTerms;

    @PositiveOrZero
    private Integer clientReviewsCount;

    // --- описание ---
    private String description;

    private String requirements;

    private String additionalRequirements;

    private String selectionStages;

    private String joiningConditions;

    // --- служебные ---
    private boolean detailsLoaded;

    @ToString.Exclude
    private String rawText;
}
