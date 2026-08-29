package ru.kryuch.krtg.searcher.repository.setting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.List;
import java.util.Optional;

public interface SettingValueRepository extends BaseAccessRepository<SettingValueEntity, Long> {

    @Query("SELECT sv FROM SettingValueEntity sv WHERE sv.setting.code = :code AND sv.userId = :userId")
    Optional<SettingValueEntity> findBySettingCodeAndUserId(@Param("code") String code, @Param("userId") Integer userId);

    @Query("SELECT sv FROM SettingValueEntity sv WHERE sv.setting.code = :code")
    List<SettingValueEntity> findBySettingCode(@Param("code") String code);

    @Query("SELECT sv.userId FROM SettingValueEntity sv WHERE sv.setting.code = :code AND sv.stringValue = :value")
    List<Integer> findUserIdBySettingCodeAndStringValue(@Param("code") String code, @Param("value") String value);

    @Query("SELECT sv FROM SettingValueEntity sv JOIN FETCH sv.setting s JOIN FETCH s.group WHERE sv.userId = :userId")
    List<SettingValueEntity> findAllByUserId(@Param("userId") Integer userId);
}