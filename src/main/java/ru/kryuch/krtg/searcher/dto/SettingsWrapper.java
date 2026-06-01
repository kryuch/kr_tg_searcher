package ru.kryuch.krtg.searcher.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@RequiredArgsConstructor
public class SettingsWrapper implements Serializable {

    private List<Setting> settings;

    public SettingsWrapper(List<Setting> settings) {
        this.settings = settings;
    }

    public void addSetting(Setting setting) {
        settings.add(setting);
    }
}
