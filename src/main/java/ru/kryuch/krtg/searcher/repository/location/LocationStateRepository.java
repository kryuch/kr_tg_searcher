package ru.kryuch.krtg.searcher.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.location.LocationStateEntity;

import java.util.Optional;

public interface LocationStateRepository extends JpaRepository<LocationStateEntity, Long> {

    Optional<LocationStateEntity> findByCode(String code);

    boolean existsByCode(String code);
}