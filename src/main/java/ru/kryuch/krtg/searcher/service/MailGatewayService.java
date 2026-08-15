package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.helper.AuthHelper;
import ru.kryuch.krtg.searcher.integration.mail.GmailClient;
import ru.kryuch.krtg.searcher.integration.mail.dto.MailSendRequest;

@Service
@RequiredArgsConstructor
public class MailGatewayService
{

    private final GmailClient gmailClient;
    private final AuthHelper authHelper;
    private final SettingService settingService;
    private final OAuth2AuthorizedClientService clientService;

    public boolean connected(OAuth2AuthenticationToken auth) {

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

        authHelper.setAuth(settingService.getUserIdByGmail(auth.getPrincipal().getAttribute("email")));

        settingService.setValueByCode(SettingConfig.GMAIL_REFRESH_TOCKEN_SETTING_CODE, refreshToken);

        return true;
    }
    public void send(String to, String body) {

        MailSendRequest mailSendRequest =
                MailSendRequest.builder()
                        .title("")
                        .body(body)
                        .build();
    }
}
