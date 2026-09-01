package ru.kryuch.krtg.searcher.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.service.AiGatewayService;
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.service.TgAccountService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SettingService settingService;
    private final AiGatewayService aiGatewayService;
    private final TgAccountService tgAccountService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        tgAccountService.init();
        aiGatewayService.init();
        setDefaultTargetUrl(SettingConfig.DEFAULT_URL);
        setAlwaysUseDefaultTargetUrl(true);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}