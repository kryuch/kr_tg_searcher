package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;
import ru.kryuch.krtg.searcher.integration.mail.GmailClient;
import ru.kryuch.krtg.searcher.integration.mail.dto.MailSendRequest;

@Service
@RequiredArgsConstructor
public class MailGatewayService
{

    private final GmailClient gmailClient;

    private final SettingService settingService;
    private final OAuth2AuthorizedClientService clientService;

    public String connected(OAuth2AuthenticationToken auth) {

        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(
                        auth.getAuthorizedClientRegistrationId(),
                        auth.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        // Получаем refresh token
        String refreshToken = null;
        if (client.getRefreshToken() != null) {
            refreshToken = client.getRefreshToken().getTokenValue();
        }

        String email = auth.getPrincipal().getAttribute("email");

  /*      GmailAccount account = repository.findByUserId(1L)
                .orElseGet(GmailAccount::new);

        account.setUserId(1L);
        account.setEmail(email);

        if (refreshToken != null) {
            account.setRefreshToken(refreshToken);
        }

        repository.save(account);
*/
        return "Gmail connected: " + email;
    }
    public void send(String to, String body) {

        MailSendRequest mailSendRequest =
                MailSendRequest.builder()
                        .title("")
                        .body(body)
                        .build();
    }
}
