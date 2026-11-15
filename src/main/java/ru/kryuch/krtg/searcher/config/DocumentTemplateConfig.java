package ru.kryuch.krtg.searcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class DocumentTemplateConfig {
    // TODO: Повесить аннотацию Value на префикс и суффикс, чтобы можно было менять их в application.properties (СПРОСИТЬ)
    public static final String TEMPLATE_ENGINE = "documentTemplateEngine";

    private static final String TEMPLATE_PREFIX = "documents/";

    private static final String TEMPLATE_SUFFIX = ".html";

    @Bean(TEMPLATE_ENGINE)
    public TemplateEngine documentTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(TEMPLATE_PREFIX);
        resolver.setSuffix(TEMPLATE_SUFFIX);
        resolver.setTemplateMode(TemplateMode.XML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(true);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
