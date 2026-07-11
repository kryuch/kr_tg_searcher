package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.mapper.IgnoreMapper;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IgnoreService {

    private final IgnoreAccessService ignoreAccessService;

    @Transactional
    public IgnoreInfo update(IgnoreInfo ignoreInfo) {
    //    ignoreAccessService.update(ignoreInfo);
        return null;
    }

    public List<IgnoreInfo> getAll() {
        return ignoreAccessService.getAll();
    }

    public IgnoreInfo get(Long id) {
        return ignoreAccessService.get(id);
    }

    public void add(IgnoreInfo ignoreInfo) {
        ignoreAccessService.add(ignoreInfo);
    }

    public void remove(Long ignoreId) {
        ignoreAccessService.delete(ignoreId);
    }
}
