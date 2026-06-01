package ru.kryuch.krtg.searcher.service;

import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.SettingsWrapper;

import java.util.List;

public interface SettingService {

    List<Setting> getAll();

    SettingsWrapper getWrapper();

    void save(SettingsWrapper wrapper);

    Setting getByCode(String code);
}
