package ru.kryuch.krtg.searcher.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kryuch.krtg.searcher.config.DocumentTemplateConfig;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDocument;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;
import ru.kryuch.krtg.searcher.type.DocumentFormat;
import ru.kryuch.krtg.searcher.helper.generator.DocumentGenerator;
import ru.kryuch.krtg.searcher.util.generator.DocumentRenderer;
import ru.kryuch.krtg.searcher.helper.generator.DocxGenerator;
import ru.kryuch.krtg.searcher.util.generator.GeneratedDocument;
import ru.kryuch.krtg.searcher.util.generator.HtmlDocumentRenderer;
import ru.kryuch.krtg.searcher.helper.generator.PdfGenerator;
import ru.kryuch.krtg.searcher.helper.generator.ResumeFontProvider;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentGeneratorServiceTest {

    private DocumentGeneratorService service;
    private ResumeFontProvider fontProvider;

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
    }

    @AfterEach
    void tearDown() {
        fontProvider.cleanup();
    }

    @Test
    void generatesDocxContainingResumeContent() throws Exception {
        GeneratedDocument document = service.generateDocument(
                ResumeDocument.from(sampleResume()), DocumentFormat.DOCX);

        assertFalse(document.content().length == 0);

        try (XWPFDocument docx = new XWPFDocument(new ByteArrayInputStream(document.content()))) {
            String text = docx.getParagraphs().stream()
                    .map(paragraph -> paragraph.getText())
                    .reduce("", (first, second) -> first + "\n" + second);

            assertTrue(text.contains("Иванова Мария"));
            assertTrue(text.contains("Разработка сервиса"));
        }
    }

    @Test
    void generatesPdfContainingResumeContent() throws Exception {
        GeneratedDocument document = service.generateDocument(
                ResumeDocument.from(sampleResume()), "pdf");

        assertFalse(document.content().length == 0);

        try (PDDocument pdf = PDDocument.load(document.content())) {
            String text = new PDFTextStripper().getText(pdf);

            assertTrue(text.contains("Иванова Мария"));
            assertTrue(text.contains("Разработка сервиса"));
        }
    }

    private ResumeDto sampleResume() {
        ResumeDto.Person person = new ResumeDto.Person();
        person.setLastName("Иванова");
        person.setFirstName("Мария");
        person.setBirthDate(LocalDate.of(1990, 5, 12));
        person.setGender(ResumeDto.Gender.FEMALE);
        person.setCitizenship("Россия");

        ResumeDto.Skill skill = new ResumeDto.Skill();
        skill.setName("Java");
        skill.setCategory(ResumeDto.SkillCategory.LANGUAGE);
        skill.setLevel(5);

        ResumeDto.Project project = new ResumeDto.Project();
        project.setOrganization("Пример");
        project.setPosition("Разработчик");
        project.setStartDate(LocalDate.of(2022, 1, 1));
        project.setDescription("Разработка сервиса обработки заявок");
        project.setTechnologies(List.of("Java", "Spring Boot"));

        ResumeDto.Profile profile = new ResumeDto.Profile();
        profile.setDirection("Java");
        profile.setGrade("Senior");
        profile.setSkills(List.of(skill));
        profile.setProjects(List.of(project));

        ResumeDto resume = new ResumeDto();
        resume.setPerson(person);
        resume.setProfiles(List.of(profile));
        resume.setEducation(List.of());
        return resume;
    }
}
