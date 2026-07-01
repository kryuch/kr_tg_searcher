package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.ChatEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    boolean existsByUsername(String username);

    @Query("SELECT c.username FROM ChatEntity c WHERE c.username in :usernames")
    Set<String> findExistingUsername(Set<String> usernames);
    Optional<ChatEntity> findByName(String name);

    @Query("SELECT c.id FROM ChatEntity c WHERE c.status > :status")
    List<Long> findIdsByStatusGreaterThan(@Param("status") Integer status);
}
