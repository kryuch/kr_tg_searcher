package ru.kryuch.krtg.searcher.repository.location;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.location.LocationCityEntity;

import java.util.List;
import java.util.Optional;

public interface LocationCityRepository extends JpaRepository<LocationCityEntity, Long> {

    @EntityGraph(attributePaths = "state")
    List<LocationCityEntity> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "state")
    List<LocationCityEntity> findByState_IdOrderByNameAsc(Long stateId);

    Optional<LocationCityEntity> findByNameAndState_Id(String name, Long stateId);
}