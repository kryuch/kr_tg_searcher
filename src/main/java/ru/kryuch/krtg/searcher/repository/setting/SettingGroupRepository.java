package ru.kryuch.krtg.searcher.repository.setting;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.setting.SettingGroupEntity;

import java.util.List;

public interface SettingGroupRepository extends CrudRepository<SettingGroupEntity, Long> {

    List<SettingGroupEntity> findByActiveTrueOrderBySortOrderAsc();

    default List<SettingGroupEntity> findAll() {
        return findByActiveTrueOrderBySortOrderAsc();
    }

}
