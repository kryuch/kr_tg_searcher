package ru.kryuch.krtg.searcher.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.service.employee.EmployeeResumeBuilderService;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class EmployeeResumeBuilderController {

    private final EmployeeResumeBuilderService employeeResumeBuilderService;

    @PostMapping(value = "/from-skills", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResumeDto buildFromSkills(@Valid @RequestBody SkillsRequest request) {
        return employeeResumeBuilderService.serializeToResumeDto(request.skills());
    }

    public record SkillsRequest(@NotBlank String skills) {
    }
}
