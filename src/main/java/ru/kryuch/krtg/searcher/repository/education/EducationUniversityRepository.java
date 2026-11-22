package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;

@Repository
public interface EducationUniversityRepository extends JpaRepository<EducationUniversityEntity, Long> {
}
