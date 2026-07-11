package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;

import java.util.List;

@NoRepositoryBean
public interface BaseAccessRepository <T extends BasedAccessEntity, ID> extends CrudRepository<T, ID> {

    List<T> findAllByUserId(Integer userId);
}