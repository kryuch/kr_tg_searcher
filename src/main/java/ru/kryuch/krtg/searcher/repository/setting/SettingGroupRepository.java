package ru.kryuch.krtg.searcher.repository.setting;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.setting.SettingGroupEntity;

import java.util.List;

public interface SettingGroupRepository extends CrudRepository<SettingGroupEntity, Long> {

    default List<SettingGroupEntity> findAll() {
        return findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    List<SettingGroupEntity> findAll(Sort sort);

}
