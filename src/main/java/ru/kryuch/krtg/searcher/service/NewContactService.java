package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.helper.ChatAccessHelper;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewContactService {

    private final ChatAccessHelper chatAccessHelper;
    private final IgnoreRepository ignoreRepository;

    public Set<String> contacts(String text) {
        Set<String> result = new HashSet<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (!ignoreRepository.existsByUsername(matcher.group(1))) {
                result.add(matcher.group(1));
            }
        }
        log.info("NewContactService::contacts (result={}", result);
        return chatAccessHelper.findUniqUsername(result, true);
    }

}