package ru.kryuch.krtg.searcher.repository.deepseek;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;

import java.util.Optional;

public interface DeepseekTaskRepository extends CrudRepository<DeepseekTaskEntity, Long> {

    Optional<DeepseekTaskEntity> findByGroupIdAndStatus(Integer groupId, DeepseekTaskStatus status);

}