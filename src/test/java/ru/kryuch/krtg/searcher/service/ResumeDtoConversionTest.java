package ru.kryuch.krtg.searcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kryuch.krtg.searcher.config.DocumentTemplateConfig;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDocument;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.exception.UnsupportedFormatException;
import ru.kryuch.krtg.searcher.helper.generator.DocxGenerator;
import ru.kryuch.krtg.searcher.helper.generator.DocumentGenerator;
import ru.kryuch.krtg.searcher.helper.generator.PdfGenerator;
import ru.kryuch.krtg.searcher.helper.generator.ResumeFontProvider;
import ru.kryuch.krtg.searcher.type.DocumentFormat;
import ru.kryuch.krtg.searcher.util.generator.DocumentRenderer;
import ru.kryuch.krtg.searcher.util.generator.GeneratedDocument;
import ru.kryuch.krtg.searcher.util.generator.HtmlDocumentRenderer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Проверяет полный путь ResumeDto -> HTML -> DOCX/PDF на файле из корня проекта.
 */
class ResumeDtoConversionTest {

    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();
    private static final Path EXAMPLE_JSON = PROJECT_ROOT.resolve("example.json");
    private static final Path OUTPUT_DIR = PROJECT_ROOT.resolve("target/resume-conversion");
    private static final String FIXTURE = "/resume/schema-valid-resume.json";

    private DocumentGeneratorService service;
    private ResumeFontProvider fontProvider;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        fontProvider = new ResumeFontProvider();
        fontProvider.init();

        DocumentRenderer renderer = new HtmlDocumentRenderer(new DocumentTemplateConfig().documentTemplateEngine());
        Map<String, DocumentGenerator> generators = Map.of(
                "pdf", new PdfGenerator(fontProvider),
                "docx", new DocxGenerator()
        );
        service = new DocumentGeneratorService(renderer, generators);

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @AfterEach
    void tearDown() {
        fontProvider.cleanup();
    }

    @Test
    void exampleJsonIsASchemaAndCannotBeParsedAsResumeDto() throws Exception {
        assumeTrue(Files.exists(EXAMPLE_JSON), "example.json не найден в корне проекта — тест пропущен");

        JsonNode schema = objectMapper.readTree(EXAMPLE_JSON.toFile());

        assertEquals("DeveloperResume", schema.path("title").asText());
        assertTrue(schema.has("$defs"), "example.json содержит определения JSON Schema");
        assertTrue(schema.path("required").isArray());

        // В файле лежит схема, а не экземпляр резюме: $schema/title/$defs и т.п. не являются полями ResumeDto.
        assertThrows(UnrecognizedPropertyException.class,
                () -> objectMapper.readValue(EXAMPLE_JSON.toFile(), ResumeDto.class));
    }

    @Test
    void convertsSchemaValidResumeJsonToDocx() throws Exception {
        ResumeDto resume = readFixture();
        assertEquals(1, resume.getSchemaVersion());
        assertNotNull(resume.getPerson());

        GeneratedDocument document = service.generateDocument(
                ResumeDocument.from(resume), DocumentFormat.DOCX);

        assertEquals("docx", document.extension());
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                document.contentType());
        assertFalse(document.content().length == 0);
        assertValidDocxPackage(document.content());

        String text = extractDocxText(document.content());
        writeArtifact("resume.docx", document.content());
        writeArtifact("resume.docx.txt", text.getBytes(StandardCharsets.UTF_8));

        assertTrue(text.contains("Иванов Алексей Сергеевич"), text);
        assertTrue(text.contains("18.03.1995 · Мужчина · Россия"), text);
        assertTrue(text.contains("ОБРАЗОВАНИЕ"), text);
        assertTrue(text.contains("Московский государственный университет"), text);
        assertTrue(text.contains("Бакалавриат, Прикладная информатика"), text);
        assertTrue(text.contains("2013 — 2017"), text);
        assertTrue(text.contains("Java · Senior"), text);
        assertTrue(text.contains("Навыки"), text);
        assertTrue(text.contains("5/5"), text);
        assertTrue(text.contains("(язык программирования)"), text);
        assertTrue(text.contains("(фреймворк)"), text);
        assertTrue(text.contains("Проекты"), text);
        assertTrue(text.contains("Сбертех — Ведущий разработчик"), text);
        assertTrue(text.contains("02.2022 — наст. время"), text);
        assertTrue(text.contains("Разрабатывал сервисы обработки заявок"), text);
        assertTrue(text.contains("Технологии: Java, Spring Boot, PostgreSQL, Kafka"), text);
        assertTrue(text.contains("Тинькофф — Java-разработчик"), text);
        assertTrue(text.contains("06.2019 — 01.2022"), text);
    }

    @Test
    void convertsSchemaValidResumeJsonToPdfForCrossCheck() throws Exception {
        ResumeDto resume = readFixture();

        GeneratedDocument document = service.generateDocument(
                ResumeDocument.from(resume), DocumentFormat.PDF);

        assertEquals("pdf", document.extension());
        assertFalse(document.content().length == 0);
        writeArtifact("resume.pdf", document.content());

        try (PDDocument pdf = PDDocument.load(document.content())) {
            String text = new PDFTextStripper().getText(pdf);
            writeArtifact("resume.pdf.txt", text.getBytes(StandardCharsets.UTF_8));

            assertTrue(text.contains("Иванов Алексей Сергеевич"), text);
            assertTrue(text.contains("Java · Senior"), text);
            assertTrue(text.contains("Сбертех — Ведущий разработчик"), text);
        }
    }

    @Test
    void emptyResumeIsConvertedWithFallbackTitle() throws Exception {
        GeneratedDocument document = service.generateDocument(
                ResumeDocument.from(new ResumeDto()), DocumentFormat.DOCX);

        String text = extractDocxText(document.content());
        assertTrue(text.contains("Резюме"), text);
        assertFalse(text.contains("ОБРАЗОВАНИЕ"), text);
    }

    @Test
    void legacyWordDocFormatIsNotSupported() {
        assertThrows(UnsupportedFormatException.class, () -> DocumentFormat.from("doc"));
    }

    private ResumeDto readFixture() throws IOException {
        try (InputStream input = getClass().getResourceAsStream(FIXTURE)) {
            assertNotNull(input, "Не найден тестовый ресурс " + FIXTURE);
            return objectMapper.readValue(input, ResumeDto.class);
        }
    }

    private String extractDocxText(byte[] content) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(new ByteArrayInputStream(content));
             XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
            return extractor.getText();
        }
    }

    private void assertValidDocxPackage(byte[] content) throws IOException {
        Set<String> entries = new HashSet<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
        }
        assertTrue(entries.contains("[Content_Types].xml"), entries.toString());
        assertTrue(entries.contains("word/document.xml"), entries.toString());
    }

    private void writeArtifact(String name, byte[] content) throws IOException {
        Files.createDirectories(OUTPUT_DIR);
        Files.write(OUTPUT_DIR.resolve(name), content);
    }
}
