package ru.kryuch.krtg.searcher.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.integration.hh.HhService;
import ru.kryuch.krtg.searcher.integration.hh.dto.HhSettingsDto;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsRequest;
import ru.kryuch.krtg.searcher.integration.hh.dto.QuestionsResponse;

@RestController
@RequestMapping("/api/hh")
@RequiredArgsConstructor
public class HhController {

    private final HhService hhService;

    @GetMapping("/settings")
    public HhSettingsDto getSettings() {
        return hhService.getSettings();
    }

    @PostMapping("/questions")
    public QuestionsResponse getQuestionsAnswers(@RequestBody QuestionsRequest questionsRequest) {
        return hhService.getQuestionsAnswers(questionsRequest);
    }
}