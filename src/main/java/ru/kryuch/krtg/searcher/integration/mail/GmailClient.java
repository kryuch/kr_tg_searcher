package ru.kryuch.krtg.searcher.integration.mail;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.activation.DataHandler;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.integration.mail.dto.MailAttachment;
import ru.kryuch.krtg.searcher.integration.mail.dto.MailSendRequest;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import static jakarta.mail.Message.RecipientType.TO;

@Component
@RequiredArgsConstructor
public class GmailClient {

    private static final JacksonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public void send(MailSendRequest request) throws Exception {

        NetHttpTransport transport =
                GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(transport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(request.getGoogleRefreshToken());

        // Получаем свежий access token из refresh token
        if (!credential.refreshToken()) {
            throw new IllegalStateException(
                    "Не удалось обновить Google access token"
            );
        }

        Gmail service = new Gmail.Builder(
                transport,
                JSON_FACTORY,
                credential
        )
                .setApplicationName("KRRG")
                .build();

        MimeMessage email = createEmail(
                request.getFrom(),
                request.getTo(),
                request.getTitle(),
                request.getBody(),
                request.getAttachments()
        );

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);

        Message message = new Message()
                .setRaw(
                        Base64.getUrlEncoder()
                                .encodeToString(buffer.toByteArray())
                );

        service.users()
                .messages()
                .send("me", message)
                .execute();
    }

    private MimeMessage createEmail(
            String from,
            String to,
            String subject,
            String body,
            List<MailAttachment> attachments
    ) throws Exception {

        Session session =
                Session.getInstance(new Properties());

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(
                TO,
                new InternetAddress(to)
        );

        email.setSubject(subject, "UTF-8");

        Multipart multipart = new MimeMultipart();

        // Текст письма
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");

        multipart.addBodyPart(textPart);

        // Вложения
        if (attachments != null) {
            for (MailAttachment attachment : attachments) {

                MimeBodyPart attachmentPart = new MimeBodyPart();

                attachmentPart.setDataHandler(
                        new DataHandler(
                                new ByteArrayDataSource(
                                        attachment.getContent(),
                                        attachment.getContentType()
                                )
                        )
                );

                attachmentPart.setFileName(attachment.getFileName());

                multipart.addBodyPart(attachmentPart);
            }
        }

        email.setContent(multipart);

        return email;
    }
}