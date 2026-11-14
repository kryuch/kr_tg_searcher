package ru.kryuch.krtg.searcher.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VacancySkillstaffRequestForEnrichmentResponse {

    private boolean exists = false;
    private Long id = 0L;
    private String url = "";
}
