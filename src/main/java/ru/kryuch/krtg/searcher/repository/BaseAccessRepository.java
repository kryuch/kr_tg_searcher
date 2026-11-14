package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseAccessRepository <T extends BasedAccessEntity, ID> extends CrudRepository<T, ID> {

    List<T> findAllByUserId(Integer userId);


    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.userId = :userId")
    Optional<T> findByIdAndUserId(@Param("id") ID id, @Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id AND e.userId = :userId")
    void deleteByIdAndUserId(@Param("id") ID id, @Param("userId") Integer userId);

}