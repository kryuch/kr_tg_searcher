package ru.kryuch.krtg.searcher.repository.development;

import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface DevelopmentSkillRepository extends TRepository<DevelopmentSkillEntity, Long> {
}
