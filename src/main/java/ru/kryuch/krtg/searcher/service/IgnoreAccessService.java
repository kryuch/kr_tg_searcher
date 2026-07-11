package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.mapper.IgnoreMapper;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

@Service
public class IgnoreAccessService extends AbstractAccessService <Long, IgnoreEntity, IgnoreInfo, IgnoreMapper, IgnoreRepository> {

    public IgnoreAccessService(IgnoreRepository ignoreRepository, IgnoreMapper ignoreMapper) {
        super(ignoreRepository, ignoreMapper, "игнорируемый контакт");
    }
}
