package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;

@Repository
public interface EducationTypeRepository extends JpaRepository<EducationTypeEntity, Long> {
}
