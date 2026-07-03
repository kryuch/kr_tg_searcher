package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.FolderEntity;

import java.util.List;

public interface FolderRepository extends CrudRepository<FolderEntity, Integer> {

    List<FolderEntity> findAllByTarget(Boolean target);
}
