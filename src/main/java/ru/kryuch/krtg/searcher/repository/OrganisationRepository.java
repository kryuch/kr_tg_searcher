package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.OrganisationEntity;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<OrganisationEntity, Long> {

    Optional<OrganisationEntity> findByName(String name);
}