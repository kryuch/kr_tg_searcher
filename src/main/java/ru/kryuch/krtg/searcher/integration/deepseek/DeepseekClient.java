package ru.kryuch.krtg.searcher.integration.deepseek;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTasksGroupEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.repository.deepseek.DeepseekTaskRepository;
import ru.kryuch.krtg.searcher.repository.deepseek.DeepseekTasksGroupRepository;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeepseekClient {

    private final DeepseekTasksGroupRepository deepseekTasksGroupRepository;
    private final DeepseekTaskRepository deepseekTaskRepository;

    private final Integer startPause = 60;
    private final Integer interval = 10;
    private final Integer retryCount = 60;

    public String execute(String task) throws InterruptedException {
        return execute(getGroup(), task);
    }

    public String execute(Integer groupId, String task) throws InterruptedException {
        Integer userId = UserUtil.getCurrentUser().getId();
        DeepseekTasksGroupEntity group = deepseekTasksGroupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("Сессия DeepSeek не найдена: " + groupId));
        return execute(group, task);
    }

    private String execute(DeepseekTasksGroupEntity group, String task) throws InterruptedException {
        DeepseekTaskEntity deepseekTaskEntity = new DeepseekTaskEntity();
        deepseekTaskEntity.setDescription(task);
        deepseekTaskEntity.setGroup(group);
        deepseekTaskEntity.setStatus(DeepseekTaskStatus.NEW);

        DeepseekTaskEntity entity = deepseekTaskRepository.save(deepseekTaskEntity);
        try {
            TimeUnit.SECONDS.sleep(startPause);
            for (int attempt = 0; attempt < retryCount; attempt++) {
                var state = deepseekTaskRepository.findStateById(entity.getId());
                if (state.isPresent() && state.get().getStatus() == DeepseekTaskStatus.DONE) {
                    return state.get().getResult();
                }
                TimeUnit.SECONDS.sleep(interval);
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;

        }
    }

    private DeepseekTasksGroupEntity getGroup() {
        Optional<DeepseekTasksGroupEntity> group =
                deepseekTasksGroupRepository.findFirstByUserIdOrderByCreatedAtDesc(UserUtil.getCurrentUser().getId());

        if (group.isEmpty()) {
            throw new BusinessException("No groups");
        }

        return group.get();
    }

    public Integer getCurrentGroupId() {
        return getGroup().getId();
    }
}
