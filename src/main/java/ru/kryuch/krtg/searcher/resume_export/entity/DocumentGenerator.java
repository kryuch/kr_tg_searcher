package ru.kryuch.krtg.searcher.resume_export.entity;

public interface DocumentGenerator {
    byte[] generate(ResumeDto resume);
    String getContentType();
    String getFileExtension();
}
