package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;

import java.util.List;

public interface TgAccountRepository extends CrudRepository<TgAccountEntity, Integer> {

    @Query("SELECT t.id FROM TgAccountEntity t")
    List<Integer> getAllIds();
}
