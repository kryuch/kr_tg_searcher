package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.entity.UserEntity;
import ru.kryuch.krtg.searcher.repository.UserRepository;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;

    public void setAuth(Integer userId) {
        SecurityContextHolder.getContext().setAuthentication(
                createAuthentication(userId)
        );
    }

    public Authentication createAuthentication(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + userId));

        CurrentUser currentUser = CurrentUser.builder()
                .id(user.getId())
                .username(user.getLogin())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                currentUser.getAuthorities()
        );
    }
}
