package ru.kryuch.krtg.searcher.service.development;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentGradeDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;
import ru.kryuch.krtg.searcher.mapper.development.DevelopmentGradeMapper;
import ru.kryuch.krtg.searcher.repository.development.DevelopmentGradeRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
public class DevelopmentGradeService extends AbstractService<Long, DevelopmentGradeEntity, DevelopmentGradeDto, DevelopmentGradeMapper, DevelopmentGradeRepository> {


    public DevelopmentGradeService(final DevelopmentGradeRepository repository, final DevelopmentGradeMapper mapper) {
        super(repository, mapper, "Grade");
    }
}
