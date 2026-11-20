package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AbstractService <
    NUMBER,
    ENTITY extends TimestampedEntity,
    DTO,
    MAPPER extends TMapper<ENTITY, DTO>,
    REPOSITORY extends JpaRepository<ENTITY, NUMBER>>{

    protected final REPOSITORY repository;
    protected final MAPPER mapper;
    protected final String entityName;

    public List<DTO> getAll() {
        return mapper.fromEntityList(repository.findAll());
    }

    public DTO get(NUMBER id) {
        return mapper.fromEntity(
            repository.findById(id)
                .orElseThrow(
                    () -> new IllegalArgumentException(String.format("{} with id={} not found: ", entityName, id)))
        );
    }

    @Transactional
    public void delete(NUMBER id) {
        repository.deleteById(id);
    }
}
