package ru.kryuch.krtg.searcher.resume_export.generator.pdf;

import com.lowagie.text.pdf.BaseFont;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextFontResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;


@Component
@Slf4j
public class ResumeFontProvider {

    private static final String FONT_FAMILY = "Arial";
    private static final String FONT_PATTERN = "classpath*:resume/fonts/*.ttf";

    private Path resourceDirectory;
    private List<Path> fontFiles = List.of();

    @PostConstruct
    public void init() throws IOException {
        if (resourceDirectory != null) {
            return;
        }

        Path root = Files.createTempDirectory("resume-resources-");
        Path fontsDirectory = Files.createDirectories(root.resolve("fonts"));

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(FONT_PATTERN);

        List<Path> copied = new ArrayList<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".ttf")) {
                continue;
            }

            Path target = fontsDirectory.resolve(filename);
            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            copied.add(target);
        }

        if (copied.isEmpty()) {
            throw new IllegalStateException("No resume fonts found at " + FONT_PATTERN);
        }

        this.fontFiles = List.copyOf(copied);
        this.resourceDirectory = root;
        log.info("Loaded {} resume font(s) into {}", fontFiles.size(), fontsDirectory);
    }

    public void registerFonts(ITextFontResolver fontResolver) throws IOException {
        for (Path fontFile : fontFiles) {
            try {
                BaseFont baseFont = BaseFont.createFont(
                        fontFile.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                fontResolver.addFont(baseFont, fontFile.toString(), FONT_FAMILY);
            } catch (Exception e) {
                throw new IOException("Failed to register resume font " + fontFile, e);
            }
        }
    }

    public String getBaseUri() {
        if (resourceDirectory == null) {
            throw new IllegalStateException("ResumeFontProvider is not initialized");
        }
        return resourceDirectory.toUri().toString();
    }

    @PreDestroy
    public void cleanup() {
        if (resourceDirectory == null) {
            return;
        }
        try (Stream<Path> paths = Files.walk(resourceDirectory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    log.warn("Failed to delete temporary resume resource {}", path, e);
                }
            });
        } catch (IOException e) {
            log.warn("Failed to cleanup temporary resume resources {}", resourceDirectory, e);
        }
    }
}
