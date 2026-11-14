package ru.kryuch.krtg.searcher.repository.deepseek;

import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTasksGroupEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.Optional;

public interface DeepseekTasksGroupRepository extends BaseAccessRepository <DeepseekTasksGroupEntity, Integer> {

    Optional<DeepseekTasksGroupEntity> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);
}
