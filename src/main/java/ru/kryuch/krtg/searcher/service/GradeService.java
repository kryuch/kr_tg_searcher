package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.GradeDto;
import ru.kryuch.krtg.searcher.entity.GradeEntity;
import ru.kryuch.krtg.searcher.mapper.GradeMapper;
import ru.kryuch.krtg.searcher.repository.GradeRepository;

@Service
public class GradeService extends AbstractService <Long, GradeEntity, GradeDto, GradeMapper, GradeRepository> {


    public GradeService(final GradeRepository repository, final GradeMapper mapper) {
        super(repository, mapper, "Grade");
    }
}
