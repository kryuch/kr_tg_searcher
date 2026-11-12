package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.Getter;
import lombok.Setter;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VacancyListItemDto {

    private Long id;
    private String externalId;
    private String url;
    private String title;
    private String status;
    private String source;
    private String organisation;
    private String category;
    private List<String> grades = new ArrayList<>();
    private BigDecimal rateResident;
    private BigDecimal rateNonResident;
    private String rateCurrency;
    private String rateUnit;
    private boolean rateOpen;
    private Integer specialistsCount;
    private WorkFormat workFormat;
    private LocalDateTime createdAt;

    public String getRateText() {
        if (rateOpen) {
            return "Открытая";
        }
        String currency = rateCurrency == null ? "" : " " + rateCurrency;
        String unit = rateUnit == null ? "" : "/" + ("HOUR".equals(rateUnit) ? "ч" : rateUnit.toLowerCase());
        List<String> parts = new ArrayList<>();
        if (rateResident != null) {
            parts.add(rateResident.stripTrailingZeros().toPlainString() + currency + unit + " (РФ)");
        }
        if (rateNonResident != null) {
            parts.add(rateNonResident.stripTrailingZeros().toPlainString() + currency + unit + " (нерезиденты)");
        }
        return parts.isEmpty() ? "—" : String.join("; ", parts);
    }

    public String getWorkFormatText() {
        return workFormat == null ? "—" : workFormat.getTitle();
    }
}