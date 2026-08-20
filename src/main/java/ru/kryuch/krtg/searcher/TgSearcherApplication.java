package ru.kryuch.krtg.searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
@EnableScheduling
public class TgSearcherApplication {

    public static void main(String[] args) throws IOException {
/*
        URL url = new URL("https://oauth2.googleapis.com/token");

        HttpsURLConnection connection =
                (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
        );

        String body =
                "client_id=" + URLEncoder.encode("325605549031-9orntk5t3vimulmrk58iherus5d6m490.apps.googleusercontent.com", UTF_8) +
                        "&client_secret=" + URLEncoder.encode("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", UTF_8) +
                        "&refresh_token=" + URLEncoder.encode("1//09atGmaDVq0rSCgYIARAAGAkSNwF-L9Ir3EnCzZnF4XVg5pX58A1e6_5cRMRUJbIli9axDRzwX7HBSCInOd-yFAXhnTolKh0rB-k", UTF_8) +
                        "&grant_type=refresh_token";

        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes(UTF_8));
        }

        System.out.println(connection.getResponseCode());
        System.out.println(
                new String(connection.getInputStream().readAllBytes(), UTF_8)
        );

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        String password1 = "H.hbr2123";
        String password2 = "Iu3Os1e_9r2";

        System.out.println("admin password: " + password1);
        System.out.println("admin hash: " + encoder.encode(password1));
        System.out.println();
        System.out.println("user password: " + password2);
        System.out.println("user hash: " + encoder.encode(password2));
*/
        SpringApplication.run(TgSearcherApplication.class, args);
    }
}
