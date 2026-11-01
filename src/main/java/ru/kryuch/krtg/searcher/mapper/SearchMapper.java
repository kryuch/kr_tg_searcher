package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.SearchParams;
import ru.kryuch.krtg.searcher.integration.tg.dto.SearchRequest;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SearchMapper {

    @Autowired
    TgAccountRepository tgAccountRepository;

    @Autowired
    TgAccountMapper tgAccountMapper;

    @Mapping(target = "tgAccounts", source = "tgAccountIds")
    public abstract SearchRequest toRequest(SearchParams params);
/*
    protected List<TgAccountInfo> getTgAccounts(List<Integer> ids) {
        Iterable<TgAccountEntity> entities = tgAccountRepository.findAllById(ids);
        return StreamSupport.stream(entities.spliterator(), false)
                .map(tgAccountMapper::fromEntity)
                .collect(Collectors.toList());
    }*/
}
