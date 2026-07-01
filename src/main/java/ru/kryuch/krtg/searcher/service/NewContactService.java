package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NewContactService {

    private final ChatRepository chatRepository;
    private final IgnoreRepository ignoreRepository;

    public Set<String> contacts(String text) {
        Set<String> result = new HashSet<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String username = "@" + matcher.group(1);
            if (!chatRepository.existsByUsername(username) && !ignoreRepository.existsByUsername(matcher.group(1))) {
                result.add(username);
            }
        }
        return result;
    }

}