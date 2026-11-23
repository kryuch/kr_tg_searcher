package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface EducationUniversityRepository extends TRepository<EducationUniversityEntity, Long> {
}
