package ru.kryuch.krtg.searcher.repository.setting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.List;
import java.util.Optional;

public interface SettingValueRepository extends BaseAccessRepository<SettingValueEntity, Long> {

    Optional<SettingValueEntity> findBySettingCodeAndUserId(String code, Integer userId);

    List<SettingValueEntity> findBySettingCode(String code);

    List<Integer> findUserIdBySettingCodeAndStringValue(String code, String value);

    @Query("SELECT sv FROM SettingValueEntity sv JOIN FETCH sv.setting s JOIN FETCH s.group WHERE sv.userId = :userId ")
    List<SettingValueEntity> findAllByUserId(@Param("userId") Integer userId);
}