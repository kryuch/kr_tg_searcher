package ru.kryuch.krtg.searcher.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffEnrichRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestForEnrichmentResponse;
import ru.kryuch.krtg.searcher.service.VacancyService;

@RestController
@RequestMapping("/api/vacancy")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {

    private final VacancyService vacancyService;


    @PostMapping("/create")
    public Long create(@RequestBody VacancySkillstaffRequestDto vacancySkillstaffRequestDto) {
        log.info("VacancyController::create ({})", vacancySkillstaffRequestDto);
        return vacancyService.add(vacancySkillstaffRequestDto);
    }

    @PostMapping("/skillstaff/new")
    public VacancySkillstaffRequestForEnrichmentResponse getNewSkillstaffItem() {
        log.info("VacancyController::getNewSkillstaffItem");
        return vacancyService.getNewSkillstafItem();
    }

    @PostMapping("/skillstaff/{id}/enrich")
    public Boolean enrich(@PathVariable("id") Long id, @RequestBody VacancySkillstaffEnrichRequestDto dto) {
        log.info("VacancyController::enrich (id={}, dtp={})", id, dto);
        return vacancyService.enrich(dto, id);
    }
}