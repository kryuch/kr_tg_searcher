package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.ChatEntity;

import java.util.Optional;

public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    boolean existsByName(String name);

    Optional<ChatEntity> findByName(String name);
}
