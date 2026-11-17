package ru.kryuch.krtg.searcher.util.generator;

@FunctionalInterface
public interface DocumentRenderer {
    RenderedHtml render(RenderableDocument document);
}
