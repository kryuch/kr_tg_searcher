package ru.kryuch.krtg.searcher.integration.mail;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.integration.mail.dto.MailSendRequest;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

import static jakarta.mail.Message.RecipientType.TO;

@Component
@RequiredArgsConstructor
public class GmailClient {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public void send(MailSendRequest request) throws Exception {

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(request.getGoogleRefreshToken());

        credential.refreshToken();

        Gmail service = new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("KRRG")
                .build();

        MimeMessage email = createEmail(
                request.getFrom(),
                request.getTo(),
                request.getTitle(),
                request.getBody()
        );

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);

        Message message = new Message()
                .setRaw(Base64.getUrlEncoder()
                        .encodeToString(buffer.toByteArray()));

        service.users().messages().send("me", message).execute();
    }

    private MimeMessage createEmail(String from, String to, String subject, String body) throws Exception {

        Session session = Session.getInstance(new Properties(), null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(TO, new InternetAddress(to));
        email.setSubject(subject, "UTF-8");
        email.setText(body, "UTF-8");

        return email;
    }

}
