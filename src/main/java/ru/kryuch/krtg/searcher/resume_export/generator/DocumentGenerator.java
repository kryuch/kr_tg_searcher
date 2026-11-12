package ru.kryuch.krtg.searcher.resume_export.generator;

import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;

public interface DocumentGenerator {
    byte[] generate(ResumeDto resume);
    String getContentType();
    String getFileExtension();
}
