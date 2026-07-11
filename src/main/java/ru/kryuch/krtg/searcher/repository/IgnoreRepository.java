package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;

public interface IgnoreRepository extends BaseAccessRepository<IgnoreEntity, Long> {

    boolean existsByUsername(String username);
}

