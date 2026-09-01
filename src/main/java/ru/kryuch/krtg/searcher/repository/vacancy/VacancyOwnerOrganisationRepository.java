package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;

import java.util.Optional;

public interface VacancyOwnerOrganisationRepository extends JpaRepository<VacancyOwnerOrganisationEntity, Long> {

    Optional<VacancyOwnerOrganisationEntity> findByName(String name);
}