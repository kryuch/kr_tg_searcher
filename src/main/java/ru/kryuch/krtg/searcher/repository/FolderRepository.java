package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.FolderEntity;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends CrudRepository<FolderEntity, Integer> {

    List<FolderEntity> findAllByTarget(Boolean target);

    default Optional<FolderEntity> findTargetFolder() {
        List<FolderEntity> folderEntities = findAllByTarget(true);
        return (folderEntities.size() > 0) ? Optional.of(folderEntities.get(0)) : Optional.empty();
    }

    @Modifying
    @Query("DELETE FROM FolderEntity t where tgId = :tgId")
    void deleteByTgId(@Param("tgId") Integer tgId);
}
