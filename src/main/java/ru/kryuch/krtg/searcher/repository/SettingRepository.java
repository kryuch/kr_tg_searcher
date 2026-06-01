package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.List;

public interface SettingRepository extends CrudRepository<SettingEntity, Long> {

    List<SettingEntity> findByCode(String code);
}
