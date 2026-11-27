package ru.kryuch.krtg.searcher.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;

@Repository
public interface DevelopmentDirectionRepository extends JpaRepository<DevelopmentSpecializationEntity, Long> {
}
