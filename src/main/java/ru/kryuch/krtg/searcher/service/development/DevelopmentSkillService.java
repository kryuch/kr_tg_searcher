package ru.kryuch.krtg.searcher.service.development;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSkillDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.mapper.development.DevelopmentSkillMapper;
import ru.kryuch.krtg.searcher.repository.development.DevelopmentSkillRepository;
import ru.kryuch.krtg.searcher.service.AbstractService;

@Service
@Transactional
public class DevelopmentSkillService extends AbstractService <Long, DevelopmentSkillEntity, DevelopmentSkillDto, DevelopmentSkillMapper, DevelopmentSkillRepository> {


    public DevelopmentSkillService(final DevelopmentSkillRepository repository, final DevelopmentSkillMapper mapper) {
        super(repository, mapper, "DevelopmentSkill");
    }
}
