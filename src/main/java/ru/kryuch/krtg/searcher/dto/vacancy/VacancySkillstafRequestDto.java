package ru.kryuch.krtg.searcher.dto.vacancy;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VacancySkillstafRequestDto {

    @Size(max = 255)
    private String externalId;

    @NotBlank
    @Size(max = 1000)
    private String url;

    @NotBlank
    @Size(max = 1000)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String category;

    @NotBlank
    @Size(max = 500)
    private String industry;

    @NotEmpty
    private Set<@NotBlank @Size(max = 20) String> grades;

    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    private BigDecimal rateResident;

    @PositiveOrZero
    private BigDecimal rateNonResident;

    @Pattern(regexp = "[A-Z]{3}")
    @Size(min = 3, max = 3)
    private String rateCurrency;

    @Size(max = 20)
    private String rateUnit;

    @Positive
    private Integer specialistsCount;

    private WorkFormat workFormat;

    @Size(max = 1000)
    private String location;

    @Positive
    private Integer durationMonths;

    private boolean indefinite;

    private String description;

    private String rawText;
}