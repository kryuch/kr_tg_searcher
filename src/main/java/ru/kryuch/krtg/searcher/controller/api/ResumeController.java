package ru.kryuch.krtg.searcher.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDocument;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.service.DocumentGeneratorService;
import ru.kryuch.krtg.searcher.service.EmployeeService;
import ru.kryuch.krtg.searcher.util.generator.GeneratedDocument;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final EmployeeService employeeService;

    private final DocumentGeneratorService documentGeneratorService;

    @GetMapping("/list")
    public List<EmployeeDto> getList() {
        return List.of(EmployeeDto.builder()
            .build());
        //return employeeService.getAll();
    }


    // Тестовый эндпоинт: POST /upload/resume?format=pdf|docx
    @PostMapping
    public ResponseEntity<byte[]> generateResume(
        @RequestParam(defaultValue = "pdf") String format,
        @Valid @RequestBody ResumeDto resume) {

        GeneratedDocument document = documentGeneratorService.generateDocument(
            ResumeDocument.from(resume), format);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename("resume." + document.extension(), StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(document.contentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .body(document.content());
    }
}
