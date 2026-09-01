package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface SettingRepository extends CrudRepository<SettingEntity, Long> {

    default List<SettingEntity> findAll() {
        return findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    List<SettingEntity> findAll(Sort sort);

    Optional<SettingEntity> findByCode(String code);

    List<SettingEntity> findByCodeIn(Collection<String> codes);
}

