package ru.kryuch.krtg.searcher.helper.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;
import ru.kryuch.krtg.searcher.exception.DocumentGenerationException;
import ru.kryuch.krtg.searcher.util.generator.RenderedHtml;

import java.io.ByteArrayOutputStream;

@Component("pdf")
@RequiredArgsConstructor
public class PdfGenerator implements DocumentGenerator {

    private final ResumeFontProvider fontProvider;

    @Override
    public byte[] generate(RenderedHtml document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            fontProvider.registerFonts(renderer.getFontResolver());
            renderer.setDocumentFromString(document.content(), fontProvider.getBaseUri());
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new DocumentGenerationException("Не удалось сформировать PDF", e);
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
