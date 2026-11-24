package ru.kryuch.krtg.searcher.repository.deepseek;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;

import java.util.Optional;

public interface DeepseekTaskRepository extends CrudRepository<DeepseekTaskEntity, Long> {

    Optional<DeepseekTaskEntity> findByGroupIdAndStatus(Integer groupId, DeepseekTaskStatus status);

    @Query("select task.status as status, task.result as result from DeepseekTaskEntity task where task.id = :id")
    Optional<TaskState> findStateById(@Param("id") Long id);

    interface TaskState {
        DeepseekTaskStatus getStatus();

        String getResult();
    }
}
