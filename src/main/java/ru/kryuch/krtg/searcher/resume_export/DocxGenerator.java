package ru.kryuch.krtg.searcher.resume_export;

import lombok.RequiredArgsConstructor;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.exception.ResumeGenerationException;
import ru.kryuch.krtg.searcher.resume_export.entity.DocumentGenerator;
import ru.kryuch.krtg.searcher.resume_export.entity.ResumeDto;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component("docx")
@RequiredArgsConstructor
public class DocxGenerator implements DocumentGenerator {

    private final ResumeHtmlRenderer htmlRenderer;

    @Override
    public byte[] generate(ResumeDto resume) {
        String xhtml = htmlRenderer.render(resume);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            WordprocessingMLPackage document = WordprocessingMLPackage.createPackage(PageSizePaper.A4, false);
            XHTMLImporterImpl importer = new XHTMLImporterImpl(document);
            List<Object> content = importer.convert(xhtml, null);
            document.getMainDocumentPart().getContent().addAll(content);
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new ResumeGenerationException("Не удалось сформировать DOCX резюме", e);
        }
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }

    @Override
    public String getFileExtension() {
        return "docx";
    }
}
