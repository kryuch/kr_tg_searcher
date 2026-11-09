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

import java.util.Objects;
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

        DeepseekTaskEntity deepseekTaskEntity = new DeepseekTaskEntity();
        deepseekTaskEntity.setDescription(task);
        deepseekTaskEntity.setGroup(getGroup());
        deepseekTaskEntity.setStatus(DeepseekTaskStatus.NEW);

        DeepseekTaskEntity entity = deepseekTaskRepository.save(deepseekTaskEntity);
        try {
            TimeUnit.SECONDS.sleep(startPause);
            boolean isDone = false;
            int iteration = 0;
            String result = null;

            do {
                Optional<DeepseekTaskEntity> actualEntity = deepseekTaskRepository.findById(entity.getId());
                if (actualEntity.isPresent() && Objects.equals(DeepseekTaskStatus.DONE, actualEntity.get().getStatus())) {
                    isDone = true;
                    result = actualEntity.get().getResult();
                }
                TimeUnit.SECONDS.sleep(interval);
                iteration++;
            }
            while (!isDone && iteration < retryCount);

            return result;
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
}
