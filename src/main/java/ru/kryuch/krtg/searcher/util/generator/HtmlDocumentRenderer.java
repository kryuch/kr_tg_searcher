package ru.kryuch.krtg.searcher.util.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.kryuch.krtg.searcher.config.DocumentTemplateConfig;

import java.util.Locale;

@Component
public class HtmlDocumentRenderer implements DocumentRenderer {
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ru");
    private final TemplateEngine templateEngine;

    public HtmlDocumentRenderer(@Qualifier(DocumentTemplateConfig.TEMPLATE_ENGINE) TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public RenderedHtml render(RenderableDocument document) {
        Context context = new Context(DEFAULT_LOCALE);
        context.setVariables(document.model());
        return new RenderedHtml(templateEngine.process(document.template(), context));
    }
}
