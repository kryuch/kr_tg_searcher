package ru.kryuch.krtg.searcher.repository.location;

import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

import java.util.Optional;

public interface LocationLanguageRepository extends TRepository<LocationLanguageEntity, Long> {

    Optional<LocationLanguageEntity> findByCode(String code);

    Optional<LocationLanguageEntity> findByName(String name);
    boolean existsByCode(String code);
}