package ru.kryuch.krtg.searcher.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.integration.deepseek.DeepseekService;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DeepseekSetting;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DsTask;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DsTaskResult;

@RestController
@RequestMapping("/api/deepseek/")
@RequiredArgsConstructor
public class DeepseekController {

    private final DeepseekService deepseekService;

    @PostMapping("/settings")
    public DeepseekSetting getSettings() {
        return deepseekService.getSettings();
    }

    @GetMapping("/task")
    public DsTask getTask(Integer groupId) {
        return deepseekService.getTask(groupId);
    }

    @PostMapping("/task/result")
    public void setTaskResult(@RequestBody DsTaskResult result) {
         deepseekService.setTaskResult(result);
    }
}