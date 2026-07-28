package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.mapper.IgnoreMapper;
import ru.kryuch.krtg.searcher.repository.IgnoreRepository;

import java.util.List;

@Service
public class IgnoreAccessService extends AbstractAccessService <Long, IgnoreEntity, IgnoreInfo, IgnoreMapper, IgnoreRepository> {

    public IgnoreAccessService(IgnoreRepository ignoreRepository, IgnoreMapper ignoreMapper) {
        super(ignoreRepository, ignoreMapper, "игнорируемый контакт");
    }

    public void add(List<IgnoreInfo> dto) {
        List <String> usernames =
                repository.findNonExistingUsernames(dto.stream().map(IgnoreInfo::getUsername).toList());

        super.add( dto.stream()
                .filter(item -> usernames.contains(item.getUsername()))
                .toList());
    }

}
