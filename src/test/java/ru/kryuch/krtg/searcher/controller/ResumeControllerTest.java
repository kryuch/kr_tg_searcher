package ru.kryuch.krtg.searcher.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.kryuch.krtg.searcher.config.DocumentTemplateConfig;
import ru.kryuch.krtg.searcher.helper.generator.DocumentGenerator;
import ru.kryuch.krtg.searcher.helper.generator.DocxGenerator;
import ru.kryuch.krtg.searcher.helper.generator.PdfGenerator;
import ru.kryuch.krtg.searcher.helper.generator.ResumeFontProvider;
import ru.kryuch.krtg.searcher.service.DocumentGeneratorService;
import ru.kryuch.krtg.searcher.util.generator.DocumentRenderer;
import ru.kryuch.krtg.searcher.util.generator.HtmlDocumentRenderer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет связку ResumeController -> DocumentGeneratorService -> PDF/DOCX.
 * Каталог для артефактов задаётся системным свойством resume.output.dir (по умолчанию target/resume-controller).
 */
class ResumeControllerTest {

    private static final String FIXTURE = "/resume/schema-valid-resume.json";
    private static final Path OUTPUT_DIR = Path.of(
            System.getProperty("resume.output.dir", "target/resume-controller"));

    private ResumeFontProvider fontProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        fontProvider = new ResumeFontProvider();
        fontProvider.init();

        DocumentRenderer renderer = new HtmlDocumentRenderer(new DocumentTemplateConfig().documentTemplateEngine());
        Map<String, DocumentGenerator> generators = Map.of(
                "pdf", new PdfGenerator(fontProvider),
                "docx", new DocxGenerator()
        );
        DocumentGeneratorService service = new DocumentGeneratorService(renderer, generators);

        mockMvc = MockMvcBuilders.standaloneSetup(new ResumeController(service)).build();
    }

    @AfterEach
    void tearDown() {
        fontProvider.cleanup();
    }

    @Test
    void generatesPdfAndDocxFromRequest() throws Exception {
        String resumeJson = readFixture();

        byte[] pdf = generate(resumeJson, "pdf", "resume.pdf");
        assertFalse(pdf.length == 0);
        try (PDDocument document = PDDocument.load(pdf)) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Иванов Алексей Сергеевич"), text);
            assertTrue(text.contains("Java · Senior"), text);
        }

        byte[] docx = generate(resumeJson, "docx", "resume.docx");
        assertFalse(docx.length == 0);
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docx));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            assertTrue(text.contains("Иванов Алексей Сергеевич"), text);
            assertTrue(text.contains("Java · Senior"), text);
        }
    }

    private byte[] generate(String resumeJson, String format, String expectedFilename) throws Exception {
        MvcResult result = mockMvc.perform(post("/upload/resume")
                        .param("format", format)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resumeJson))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString(expectedFilename)))
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        Files.createDirectories(OUTPUT_DIR);
        Files.write(OUTPUT_DIR.resolve(expectedFilename), content);
        return content;
    }

    private String readFixture() throws Exception {
        try (InputStream input = getClass().getResourceAsStream(FIXTURE)) {
            assertNotNull(input, "Не найден тестовый ресурс " + FIXTURE);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
