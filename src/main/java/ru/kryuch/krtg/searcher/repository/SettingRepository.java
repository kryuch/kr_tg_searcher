package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;

import java.util.List;

public interface SettingRepository extends BaseAccessRepository<SettingEntity, Long> {

    List<SettingEntity> findByCode(String code);
    List<SettingEntity> findByCodeAndUserId(String code, Integer userId);
}
