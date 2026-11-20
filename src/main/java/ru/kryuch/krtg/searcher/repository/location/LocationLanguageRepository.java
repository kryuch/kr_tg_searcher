package ru.kryuch.krtg.searcher.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.location.LocationLanguageEntity;

import java.util.Optional;

public interface LocationLanguageRepository extends JpaRepository<LocationLanguageEntity, Long> {

    Optional<LocationLanguageEntity> findByCode(String code);

    boolean existsByCode(String code);
}