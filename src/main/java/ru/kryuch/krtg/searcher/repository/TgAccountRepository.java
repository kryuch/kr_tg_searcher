package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;

import java.util.List;

public interface TgAccountRepository extends BaseAccessRepository<TgAccountEntity, Integer> {

    @Query("SELECT t.id FROM TgAccountEntity t")
    List<Integer> getAllIds();

    @Modifying
    @Query("UPDATE TgAccountEntity t set t.isAuth = true WHERE id = :id")
    void setAuth(@Param("id") Integer id);
}
