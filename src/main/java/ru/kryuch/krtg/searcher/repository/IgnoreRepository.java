package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IgnoreRepository extends BaseAccessRepository<IgnoreEntity, Long> {

    boolean existsByUsername(String username);

    @Query("SELECT u.username FROM IgnoreEntity u WHERE u.username IN :usernames")
    List<String> findExistingUsernames(@Param("usernames") List<String> usernames);

    default List<String> findNonExistingUsernames(Set<String> usernames) {
        return findNonExistingUsernames(usernames.stream().toList());
    }

    default List<String> findNonExistingUsernames(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> existing = findExistingUsernames(usernames);
        return usernames.stream()
                .filter(username -> !existing.contains(username))
                .collect(Collectors.toList());
    }
}

