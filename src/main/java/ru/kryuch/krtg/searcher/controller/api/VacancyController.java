package ru.kryuch.krtg.searcher.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestDto;
import ru.kryuch.krtg.searcher.service.VacancyService;

@RestController
@RequestMapping("/api/vacancy")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/create")
    public Long create(@RequestBody VacancySkillstaffRequestDto vacancySkillstaffRequestDto) {
        return vacancyService.add(vacancySkillstaffRequestDto);
    }
}