package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;

@Repository
public interface EducationSpecialtyRepository extends JpaRepository<EducationSpecialtyEntity, Long> {
}
