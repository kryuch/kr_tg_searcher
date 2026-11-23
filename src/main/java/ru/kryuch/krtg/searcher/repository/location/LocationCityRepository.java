package ru.kryuch.krtg.searcher.repository.location;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

import java.util.List;
import java.util.Optional;

public interface LocationCityRepository extends TRepository<LocationCityEntity, Long> {

    @EntityGraph(attributePaths = "state")
    List<LocationCityEntity> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "state")
    List<LocationCityEntity> findByStateIdOrderByNameAsc(Long stateId);

    Optional<LocationCityEntity> findByNameAndStateId(String name, Long stateId);
}