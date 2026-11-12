package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.entity.ApiToken;
import ru.kryuch.krtg.searcher.repository.ApiTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    private final ApiTokenRepository apiTokenRepository;

    public ApiToken issueToken(String username, String application) {
        ApiToken token = new ApiToken();
        token.setToken(KeyGenerators.string().generateKey() + KeyGenerators.string().generateKey());
        token.setUsername(username);
        token.setApplication(application);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        return apiTokenRepository.save(token);
    }

    public java.util.Optional<ApiToken> validate(String rawToken) {
        return apiTokenRepository.findByToken(rawToken)
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()));
    }

    public void revoke(String rawToken) {
        apiTokenRepository.deleteByToken(rawToken);
    }
}
