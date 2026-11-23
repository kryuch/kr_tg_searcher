package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface EducationTypeRepository extends TRepository<EducationTypeEntity, Long> {
}
