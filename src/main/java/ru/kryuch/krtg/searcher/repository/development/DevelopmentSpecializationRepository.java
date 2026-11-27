package ru.kryuch.krtg.searcher.repository.development;

import org.springframework.stereotype.Repository;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;
import ru.kryuch.krtg.searcher.repository.TRepository;

@Repository
public interface DevelopmentSpecializationRepository extends TRepository<DevelopmentSpecializationEntity, Long> {
}
