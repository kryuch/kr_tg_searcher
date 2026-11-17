package ru.kryuch.krtg.searcher.helper.generator;

import ru.kryuch.krtg.searcher.util.generator.RenderedHtml;

public interface DocumentGenerator {
    byte[] generate(RenderedHtml document);
    String getContentType();
    String getFileExtension();
}
