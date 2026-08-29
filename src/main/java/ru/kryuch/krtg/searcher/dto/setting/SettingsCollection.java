package ru.kryuch.krtg.searcher.dto.setting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.dto.Setting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SettingsCollection {

    /**
     * Плоский список — командный объект формы (th:object),
     * Spring MVC биндит поля по индексу: settings[i].intValue и т.п.
     */
    private List<Setting> settings = new ArrayList<>();

    public SettingsCollection(List<Setting> values) {
        if (!CollectionUtils.isEmpty(values)) {
            this.settings = values;
        }
    }

    /**
     * Код группы -> индексы в settings, относящиеся к этой группе.
     * Только для отображения; порядок и title самих групп задаёт
     * отдельный список групп (SettingGroupService.getAll()), передаваемый
     * в шаблон отдельным атрибутом — здесь только группировка по коду.
     */
    public Map<String, List<Integer>> getGroupedIndices() {
        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (int i = 0; i < settings.size(); i++) {
            String groupCode = settings.get(i).getGroup();
            result.computeIfAbsent(groupCode, g -> new ArrayList<>()).add(i);
        }
        return result;
    }
}