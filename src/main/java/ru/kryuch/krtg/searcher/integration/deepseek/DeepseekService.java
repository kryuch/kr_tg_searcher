package ru.kryuch.krtg.searcher.integration.deepseek;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.entity.deepseek.DeepseekTaskEntity;
import ru.kryuch.krtg.searcher.helper.DeepseekHelper;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DeepseekSetting;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DsTask;
import ru.kryuch.krtg.searcher.integration.deepseek.dto.DsTaskResult;
import ru.kryuch.krtg.searcher.integration.deepseek.mapper.DeepseekMapper;
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.type.DeepseekTaskStatus;
import ru.kryuch.krtg.searcher.type.SettingCode;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeepseekService {

    private final SettingService settingService;
    private final DeepseekHelper deepseekHelper;
    private final DeepseekMapper deepseekMapper;

    public DeepseekSetting getSettings() {
        ;
        return new DeepseekSetting(
                deepseekHelper.createGroup(UserUtil.getCurrentUser().getId()),
                settingService.getByCode(SettingCode.DEEPSEEK_PAUSE.getCode()).getIntValue()
        );
    }

    @Transactional
    public DsTask getTask(Integer groupId) {
        Optional<DeepseekTaskEntity> deepseekTask = deepseekHelper.getTask(groupId);
        if (deepseekTask.isPresent()) {
            deepseekTask.get().setStatus(DeepseekTaskStatus.PROCESSING);
            return deepseekMapper.toDto(deepseekTask.get());
        } else {
            return DsTask.builder().groupId(groupId).isReady(false).build();
        }
    }

    public void setTaskResult(DsTaskResult result) {
        deepseekHelper.setTaskResult(result.getValue(), result.getId());
    }

}