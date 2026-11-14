package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.GradeDto;
import ru.kryuch.krtg.searcher.mapper.GradeMapper;
import ru.kryuch.krtg.searcher.repository.GradeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    public List<GradeDto> getAll() {
        return gradeMapper.fromEntityList(gradeRepository.findAll());
    }
}
