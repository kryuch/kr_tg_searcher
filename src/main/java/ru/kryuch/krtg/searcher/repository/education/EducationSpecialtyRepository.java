package ru.kryuch.krtg.searcher.repository.education;

import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface EducationSpecialtyRepository extends TRepository<EducationSpecialtyEntity, Long> {
}
