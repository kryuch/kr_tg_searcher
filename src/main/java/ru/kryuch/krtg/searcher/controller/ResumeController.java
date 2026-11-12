package ru.kryuch.krtg.searcher.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.service.ResumeGeneratorService;
import ru.kryuch.krtg.searcher.dto.resume.SendResumeRequest;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeGeneratorService resumeGeneratorService;


    // Тестовыц эндпоинт, в последствии переделаю/уберу
    @PostMapping("/upload/resume")
    public ResponseEntity<byte[]> uploadResume(@Valid @RequestBody SendResumeRequest request) {
        ResumeGeneratorService.GeneratedDocument document = resumeGeneratorService.generateResume(request);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename("resume." + document.extension(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(document.content());
    }

    // Вынести в advice

//    @ExceptionHandler(UnsupportedFormatException.class)
//    public ResponseEntity<Map<String, String>> handleUnsupportedFormat(UnsupportedFormatException exception) {
//        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
//    }
}
