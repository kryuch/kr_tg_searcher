package ru.kryuch.krtg.searcher.service.vacancy;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionOptionDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionOptionEntity;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyQuestionMapper;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionRepository;
import ru.kryuch.krtg.searcher.type.QuestionType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacancyQuestionService {

    // Типы вопросов, для которых допустимы варианты ответа.
    // TODO: сверить с реальными значениями QuestionType
    private static final Set<QuestionType> SUPPORTS_OPTIONS =
            EnumSet.of(QuestionType.SINGLE_OPTION, QuestionType.MULTIPLE_OPTION);

    private final VacancyQuestionRepository questionRepository;
    private final VacancyQuestionMapper questionMapper;

    public List<VacancyQuestionDto> getAll() {
        return questionMapper.fromEntityList(questionRepository.findAll());
    }

    public VacancyQuestionDto getById(Integer id) {
        return questionMapper.fromEntity(
                questionRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Question not found: " + id))
        );
    }

    @Transactional
    public VacancyQuestionDto add(VacancyQuestionDto dto) {
        validateOptions(dto);

        VacancyQuestionEntity entity = questionMapper.toEntity(dto);
        // мэппер игнорирует question у опций — проставляем родителя вручную
        entity.getOptions().forEach(opt -> opt.setQuestion(entity));

        if (!SUPPORTS_OPTIONS.contains(entity.getType())) {
            entity.getOptions().clear();
        }

        return questionMapper.fromEntity(questionRepository.save(entity));
    }

    @Transactional
    public VacancyQuestionDto update(VacancyQuestionDto dto, Integer questionId) {
        validateOptions(dto);

        VacancyQuestionEntity entity = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        entity.setText(dto.getText());
        entity.setType(dto.getType());
        entity.setRequired(dto.isRequired());

        syncOptions(entity, dto.getOptions());

        // save не обязателен для управляемой сущности в рамках транзакции,
        // но оставляем явно для читаемости
        return questionMapper.fromEntity(questionRepository.save(entity));
    }

    @Transactional
    public void remove(Integer questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question not found: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    /**
     * Синхронизирует опции сущности со списком из DTO:
     * - опции с id, которых нет во входящем списке — удаляются (orphanRemoval подхватит на flush);
     * - опции с id, которые есть — обновляют text/sortOrder;
     * - опции без id (новые) — добавляются.
     * Если тип вопроса не поддерживает опции — список принудительно очищается.
     */
    private void syncOptions(VacancyQuestionEntity entity, List<VacancyQuestionOptionDto> incoming) {
        if (!SUPPORTS_OPTIONS.contains(entity.getType())) {
            entity.getOptions().clear();
            return;
        }

        if (incoming == null) {
            incoming = List.of();
        }

        Map<Integer, VacancyQuestionOptionEntity> existingById = new HashMap<>();
        for (VacancyQuestionOptionEntity opt : entity.getOptions()) {
            existingById.put(opt.getId(), opt);
        }

        Set<Integer> incomingIds = incoming.stream()
                .map(VacancyQuestionOptionDto::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        // удаляем те, которых больше нет во входящем списке
        entity.getOptions().removeIf(opt -> !incomingIds.contains(opt.getId()));

        int order = 0;
        for (VacancyQuestionOptionDto optDto : incoming) {
            if (optDto.getId() != null && existingById.containsKey(optDto.getId())) {
                VacancyQuestionOptionEntity existing = existingById.get(optDto.getId());
                existing.setText(optDto.getText());
                existing.setSortOrder(order++);
            } else {
                VacancyQuestionOptionEntity created = VacancyQuestionOptionEntity.builder()
                        .text(optDto.getText())
                        .sortOrder(order++)
                        .build();
                entity.addOption(created);
            }
        }
    }

    private void validateOptions(VacancyQuestionDto dto) {
        boolean hasOptions = dto.getOptions() != null && !dto.getOptions().isEmpty();
        if (hasOptions && !SUPPORTS_OPTIONS.contains(dto.getType())) {
            throw new IllegalArgumentException(
                    "Question type " + dto.getType() + " does not support options");
        }
    }
}