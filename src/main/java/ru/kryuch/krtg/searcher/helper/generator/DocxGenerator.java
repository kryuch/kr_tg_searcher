package ru.kryuch.krtg.searcher.helper.generator;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.exception.DocumentGenerationException;
import ru.kryuch.krtg.searcher.util.generator.RenderedHtml;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component("docx")
public class DocxGenerator implements DocumentGenerator {

    @Override
    public byte[] generate(RenderedHtml renderedHtml) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            WordprocessingMLPackage document = WordprocessingMLPackage.createPackage(PageSizePaper.A4, false);
            XHTMLImporterImpl importer = new XHTMLImporterImpl(document);
            List<Object> content = importer.convert(renderedHtml.content(), null);
            document.getMainDocumentPart().getContent().addAll(content);
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new DocumentGenerationException("Не удалось сформировать DOCX резюме", e);
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
