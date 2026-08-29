package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.ExtensionTaskEntity;
import ru.kryuch.krtg.searcher.type.ExtensionTaskStatus;

import java.util.List;

public interface ExtensionTaskRepository
        extends JpaRepository<ExtensionTaskEntity, Long> {

    List<ExtensionTaskEntity> findByUserIdAndStatusOrderByCreatedAtAsc(
            Long userId,
            ExtensionTaskStatus status
    );
}