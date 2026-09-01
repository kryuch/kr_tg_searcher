package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAccessService<
        NUMBER,
        ENTITY extends BasedAccessEntity,
        DTO,
        MAPPER extends TMapper<ENTITY, DTO>,
        REPOSITORY extends BaseAccessRepository<ENTITY, NUMBER>> {

    protected final REPOSITORY repository;
    protected final MAPPER mapper;
    private final String entityName;


    public List<DTO> getAll() {
        return mapper.fromEntityList(repository.findAllByUserId(getCurrentUserId()));
    }

    public DTO get(NUMBER id) {
        return mapper.fromEntity(
                repository.findByIdAndUserId(id, getCurrentUserId())
                        .orElseThrow(() -> new BusinessException(
                                String.format("Не существует сущности <<%s>> с id=%s", entityName, id)
                        ))
        );
    }

    public void add(DTO dto) {
        ENTITY entity = mapper.toEntity(dto);
        entity.setUserId(getCurrentUserId());
        repository.save(entity);
    }

    public void add(List<DTO> dto) {
        Integer userId = getCurrentUserId();
        repository.saveAll(
                mapper.toEntityList(dto).stream()
                        .map(item -> {
                            item.setUserId(userId);
                            return item;
                        })
                        .toList()
        );
    }

    public void update(DTO dto, NUMBER id) {
        ENTITY entity =
                repository.findByIdAndUserId(id, getCurrentUserId())
                        .orElseThrow(() -> new BusinessException(
                                String.format("Не существует сущности <<%s>> с id=%s", entityName, id)
                        ));
        repository.save(mapper.mergeToEntity(dto, entity));
    }

    public void delete(NUMBER id) {
        repository.deleteByIdAndUserId(id, getCurrentUserId());
    }

    public CurrentUser getCurrentUser() {
        return UserUtil.getCurrentUser();
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

}
