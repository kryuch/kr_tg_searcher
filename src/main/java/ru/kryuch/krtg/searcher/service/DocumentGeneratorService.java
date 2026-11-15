package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.exception.UnsupportedFormatException;
import ru.kryuch.krtg.searcher.type.DocumentFormat;
import ru.kryuch.krtg.searcher.helper.generator.DocumentGenerator;
import ru.kryuch.krtg.searcher.util.generator.DocumentRenderer;
import ru.kryuch.krtg.searcher.util.generator.GeneratedDocument;
import ru.kryuch.krtg.searcher.util.generator.RenderableDocument;
import ru.kryuch.krtg.searcher.util.generator.RenderedHtml;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {
    private final DocumentRenderer renderer;
    private final Map<String, DocumentGenerator> documentGenerators;

    public GeneratedDocument generateDocument(RenderableDocument document, String format) {
        return generateDocument(document, DocumentFormat.from(format));
    }

    public GeneratedDocument generateDocument(RenderableDocument document, DocumentFormat format) {
        DocumentGenerator generator = resolveGenerator(format);
        RenderedHtml renderedHtml = renderer.render(document);
        byte[] content = generator.generate(renderedHtml);
        return new GeneratedDocument(content, generator.getContentType(), generator.getFileExtension());
    }

    private DocumentGenerator resolveGenerator(DocumentFormat format) {
        String key = format.key();
        return Optional.ofNullable(documentGenerators.get(key))
                .orElseThrow(() -> new UnsupportedFormatException(key));
    }
}
