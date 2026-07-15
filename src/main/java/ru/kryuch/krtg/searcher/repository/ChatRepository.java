package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.ChatEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    boolean existsByUsername(String username);

    Optional<ChatEntity> findByName(String name);

    @Query("SELECT c.id FROM ChatEntity c WHERE c.status > :status")
    List<Long> findIdsByStatusGreaterThan(@Param("status") Integer status);

    default Set<String> findExistingUsername(Set<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Set.of();
        }

        Set<String> lowerCaseUsernames = usernames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return ((List<ChatEntity>) findAll()).stream()
                .map(ChatEntity::getUsername)
                .filter(username -> username != null && lowerCaseUsernames.contains(username.toLowerCase()))
                .map(item -> item.toLowerCase())
                .collect(Collectors.toSet());
    }
}