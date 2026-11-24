package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.stereotype.Repository;
import ru.kryuch.krtg.searcher.entity.education.EducationDegreeEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface EducationDegreeRepository extends TRepository<EducationDegreeEntity, Long> {
}
