package ru.kryuch.krtg.searcher.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTasksGroupEntity;
import ru.kryuch.krtg.searcher.repository.deepseek.DeepseekTaskRepository;
import ru.kryuch.krtg.searcher.repository.deepseek.DeepseekTasksGroupRepository;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeepseekHelper {

    private final DeepseekTasksGroupRepository deepseekTasksGroupRepository;
    private final DeepseekTaskRepository deepseekTaskRepository;

    public Integer createGroup(Integer userId) {
        DeepseekTasksGroupEntity deepseekTasksGroupEntity = new DeepseekTasksGroupEntity();
        deepseekTasksGroupEntity.setUserId(userId);
        deepseekTasksGroupEntity = deepseekTasksGroupRepository.save(deepseekTasksGroupEntity);
        return deepseekTasksGroupEntity.getId();
    }

    public Optional<DeepseekTaskEntity> getTask(Integer groupId) {
        return deepseekTaskRepository.findByGroupIdAndStatus(groupId, DeepseekTaskStatus.NEW);
    }

    @Transactional
    public void setTaskResult(String result, Long id) {
        DeepseekTaskEntity deepseekTaskEntity =
                deepseekTaskRepository.findById(id).orElseThrow(() -> new RuntimeException("Not correct id"));
        deepseekTaskEntity.setResult(result);
        deepseekTaskEntity.setStatus(DeepseekTaskStatus.DONE);
    }
}
