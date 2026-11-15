package ru.kryuch.krtg.searcher.dto.resume;

import ru.kryuch.krtg.searcher.util.generator.RenderableDocument;

import java.util.Map;

public record ResumeDocument(ResumeView view) implements RenderableDocument {
    private static final String TEMPLATE = "resume/template";

    public static ResumeDocument from(ResumeDto resume) {
        return new ResumeDocument(ResumeView.from(resume));
    }

    @Override
    public String template() {
        return TEMPLATE;
    }

    @Override
    public Map<String, Object> model() {
        return Map.of("view", view);
    }
}
