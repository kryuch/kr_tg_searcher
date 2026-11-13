package ru.kryuch.krtg.searcher.util.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;
import ru.kryuch.krtg.searcher.exception.ResumeGenerationException;
import ru.kryuch.krtg.searcher.resume_export.html.ResumeHtmlRenderer;
import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;

import java.io.ByteArrayOutputStream;

@Component("pdf")
@RequiredArgsConstructor
public class PdfGenerator implements DocumentGenerator {

    private final ResumeHtmlRenderer htmlRenderer;
    private final ResumeFontProvider fontProvider;

    @Override
    public byte[] generate(ResumeDto resume) {
        String xhtml = htmlRenderer.render(resume);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            fontProvider.registerFonts(renderer.getFontResolver());
            renderer.setDocumentFromString(xhtml, fontProvider.getBaseUri());
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new ResumeGenerationException("Не удалось сформировать PDF резюме", e);
        }
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() {
        return "pdf";
    }
}
