package ru.kryuch.krtg.searcher.resume_export;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.resume_export.model.ResumeView;

import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Component
public class ResumeHtmlRenderer {
    // Вынести SpringTemplateEngine в бин, если будет необходимость использовать его в других местах
    private static final String TEMPLATE_NAME = "template";
    private static final String TEMPLATE_PREFIX = "resume/";
    private static final String TEMPLATE_SUFFIX = ".html";

    private final TemplateEngine templateEngine;

    public ResumeHtmlRenderer() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(TEMPLATE_PREFIX);
        resolver.setSuffix(TEMPLATE_SUFFIX);
        resolver.setTemplateMode(TemplateMode.XML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false); // Включаем кеширование, если соло шаблон

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        this.templateEngine = engine;
    }

    public String render(ResumeDto resume) {
        Context context = new Context(Locale.forLanguageTag("ru"));
        context.setVariable("view", ResumeView.from(resume));
        return templateEngine.process(TEMPLATE_NAME, context);
    }
}
