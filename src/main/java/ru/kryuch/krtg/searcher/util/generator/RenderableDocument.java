package ru.kryuch.krtg.searcher.util.generator;

import java.util.Map;

public interface RenderableDocument {
    String template();

    Map<String, Object> model();
}
