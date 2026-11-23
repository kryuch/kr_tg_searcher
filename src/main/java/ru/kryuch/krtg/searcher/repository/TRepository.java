package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface TRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    // Spring Data сгенерирует этот метод для каждой сущности
    List<T> findByNameContainingIgnoreCase(String namePart);

    // Опционально: default-метод для ограничения количества (автокомплит)
    default List<T> findTop10ByNameContainingIgnoreCase(String namePart) {
        List<T> all = findByNameContainingIgnoreCase(namePart);
        return all.size() > 10 ? all.subList(0, 10) : all;
    }
}