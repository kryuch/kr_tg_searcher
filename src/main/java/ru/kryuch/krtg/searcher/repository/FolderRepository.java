package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.FolderEntity;

public interface FolderRepository extends CrudRepository<FolderEntity, Integer> {
}
