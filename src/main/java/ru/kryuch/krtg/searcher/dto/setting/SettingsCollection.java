package ru.kryuch.krtg.searcher.dto.setting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.dto.SettingDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SettingsCollection {

    private List<SettingDto> settings = new ArrayList<>();

    public SettingsCollection(List<SettingDto> values) {
        if (!CollectionUtils.isEmpty(values)) {
            this.settings = values;
        }
    }

    public Map<String, List<Integer>> getGroupedIndices() {
        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (int i = 0; i < settings.size(); i++) {
            String groupCode = settings.get(i).getGroup();
            result.computeIfAbsent(groupCode, g -> new ArrayList<>()).add(i);
        }
        return result;
    }
}