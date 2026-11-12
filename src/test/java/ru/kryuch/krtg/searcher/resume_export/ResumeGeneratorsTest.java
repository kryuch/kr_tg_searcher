package ru.kryuch.krtg.searcher.resume_export;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kryuch.krtg.searcher.resume_export.entity.ResumeDto;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeGeneratorsTest {

    private ResumeHtmlRenderer htmlRenderer;
    private ResumeFontProvider fontProvider;

    @BeforeEach
    void setUp() throws Exception {
        htmlRenderer = new ResumeHtmlRenderer();
        fontProvider = new ResumeFontProvider();
        fontProvider.init();
    }

    @AfterEach
    void tearDown() {
        fontProvider.cleanup();
    }

    @Test
    void generatesDocxContainingResumeContent() throws Exception {
        byte[] generated = new DocxGenerator(htmlRenderer).generate(sampleResume());

        assertFalse(generated.length == 0);

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generated))) {
            String text = document.getParagraphs().stream()
                    .map(paragraph -> paragraph.getText())
                    .reduce("", (first, second) -> first + "\n" + second);

            assertTrue(text.contains("Иванова Мария"));
            assertTrue(text.contains("Разработка сервиса"));
        }
    }

    @Test
    void generatesPdfContainingResumeContent() throws Exception {
        byte[] generated = new PdfGenerator(htmlRenderer, fontProvider).generate(sampleResume());

        assertFalse(generated.length == 0);

        try (PDDocument document = PDDocument.load(generated)) {
            String text = new PDFTextStripper().getText(document);

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
