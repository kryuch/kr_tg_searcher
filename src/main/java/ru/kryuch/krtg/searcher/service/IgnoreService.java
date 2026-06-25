package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    private final IgnoreRepository ignoreRepository;
    private final IgnoreMapper ignoreMapper;

    @Transactional
    public IgnoreInfo update(IgnoreInfo ignoreInfo) {
        IgnoreInfo oldValue = get(ignoreInfo.getId());
        oldValue.setUsername(ignoreInfo.getUsername());
        return ignoreMapper.fromEntity(ignoreRepository.save(ignoreMapper.toEntity(oldValue)));
    }

    @Transactional(readOnly = true)
    public List<IgnoreInfo> getAll() {
        return ignoreMapper.fromEntityList(Streamable.of(ignoreRepository.findAll()).toList());
    }

    @Transactional(readOnly = true)
    public IgnoreInfo get(Long id) {
        return ignoreMapper.fromEntity(ignoreRepository.findById(id).orElse(new IgnoreEntity()));
    }

    public void add(IgnoreInfo ignoreInfo) {
        ignoreRepository.save(ignoreMapper.toEntity(ignoreInfo));
    }
}
