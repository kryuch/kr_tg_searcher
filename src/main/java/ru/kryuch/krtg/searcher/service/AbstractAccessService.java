package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AbstractAccessService <
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
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                String.format("Не существует сущности <<%s>> с id=%d", entityName, id)
                        ))
        );
    }

    public void add(DTO dto) {
        ENTITY entity = mapper.toEntity(dto);
        entity.setUserId(getCurrentUserId());
        repository.save(entity);
    }

    public void update(DTO dto, NUMBER id) {/*
        ENTITY entity = repository.mapper.toEntity(dto);
        entity.setId(dto);
        ENTITY entity = mapper.toEntity(dto);
        entity.setUserId(getCurrentUserId());
        repository.save(entity);

        IgnoreInfo oldValue = get(ignoreInfo.getId());
        oldValue.setUsername(ignoreInfo.getUsername());
        return ignoreMapper.fromEntity(ignoreRepository.save(ignoreMapper.toEntity(oldValue)));*/
    }

    public void delete(NUMBER id) {
        repository.deleteById(id);
    }

    public CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CurrentUser) {
            return (CurrentUser) principal;
        }
        throw new IllegalStateException("Principal не является CurrentUser");
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

}
