package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.exception.UnsupportedFormatException;
import ru.kryuch.krtg.searcher.util.generator.DocumentGenerator;
import ru.kryuch.krtg.searcher.dto.resume.SendResumeRequest;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeGeneratorService {
    private final Map<String, DocumentGenerator> documentGenerators;

    public GeneratedDocument generateResume(SendResumeRequest dto) {
        DocumentGenerator generator = resolveGenerator(dto.format());
        byte[] content = generator.generate(dto.resume());
        // Здесь отправка в ТГ, но пока просто возвращаем результат
        return new GeneratedDocument(content, generator.getContentType(), generator.getFileExtension());
    }

    private DocumentGenerator resolveGenerator(String format) {
        String key = format.trim().toLowerCase();
        return Optional.ofNullable(documentGenerators.get(key))
                .orElseThrow(() -> new UnsupportedFormatException(format));
    }

    public record GeneratedDocument(byte[] content, String contentType, String extension) {}
}
