package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.List;

public interface SettingRepository extends BaseAccessRepository<SettingEntity, Long> {

    List<SettingEntity> findByCode(String code);
    List<SettingEntity> findByCodeAndUserId(String code, Integer userId);

    @Query("SELECT t.userId FROM SettingEntity t where code = :code and value = :value")
    List<Integer> findUserIdByCodeAndValue(String code, String value);
}
