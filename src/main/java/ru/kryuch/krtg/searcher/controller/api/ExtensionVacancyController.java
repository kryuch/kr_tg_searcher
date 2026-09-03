package ru.kryuch.krtg.searcher.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.MeDto;
import ru.kryuch.krtg.searcher.dto.VacancyRequest;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.integration.hh.dto.VacancyAnalysisResponse;
import ru.kryuch.krtg.searcher.service.vacancy.VacancyResponseAccessService;

@RestController
@RequestMapping("/api/extension/vacancies")
@RequiredArgsConstructor
public class ExtensionVacancyController {

    private final VacancyResponseAccessService vacancyService;

    @PostMapping("/me")
    public MeDto me() {
        return null;//vacancyAnalysisService.analyze(request);
    }

    @PostMapping
    public VacancyQuestionAnswerDto create(
            @RequestBody VacancyQuestionAnswerRequestDto request
    ) {

        return null;//vacancyService.processVacancy(
            //    request
     //   );
    }

    @PostMapping("/vacancies")
    public VacancyAnalysisResponse analyze(
            @RequestBody VacancyRequest request
    ) {
        return null;//vacancyAnalysisService.analyze(request);
    }

    @PostMapping("/vacancies/by-external-id/{externalId}")
    public VacancyAnalysisResponse byExternalId(Integer externalId) {
        return null;// vacancyAnalysisService.analyze(request);
    }
/*
    @PostMapping("/analyze")
    public VacancyAnalysisResponse analyze(
            @RequestBody VacancyAnalysisRequest request
    ) {
        return vacancyAnalysisService.analyze(request);
    }*/
}