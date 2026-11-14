package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface TgUserRepository extends CrudRepository<TgUserEntity, Long> {

    boolean existsByUsername(String username);

    Optional<TgUserEntity> findByName(String name);

    Optional<TgUserEntity> findByUsername(String name);

    @Query("select u from TgUserEntity u where lower(u.username) in :usernames")
    Set <TgUserEntity> findAllByUsernameIn(Set<String> usernames);

    default Set<String> findExistingUsername(Set<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Set.of();
        }

        Set<String> lowerCaseUsernames = usernames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return ((List<TgUserEntity>) findAll()).stream()
                .map(TgUserEntity::getUsername)
                .filter(username -> username != null && lowerCaseUsernames.contains(username.toLowerCase()))
                .map(item -> item.toLowerCase())
                .collect(Collectors.toSet());
    }
}