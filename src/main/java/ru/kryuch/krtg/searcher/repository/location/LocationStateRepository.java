package ru.kryuch.krtg.searcher.repository.location;

import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

import java.util.Optional;

public interface LocationStateRepository extends TRepository<LocationStateEntity, Long> {

    Optional<LocationStateEntity> findByCode(String code);

    boolean existsByCode(String code);
}