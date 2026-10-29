package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyQuestionAnswerRequestDto;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionAnswerOptionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyQuestionOptionEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyQuestionAnswerMapper;
import ru.kryuch.krtg.searcher.mapper.vacancy.VacancyQuestionAnswerOptionMapper;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionAnswerRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyQuestionRepository;
import ru.kryuch.krtg.searcher.type.QuestionType;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacancyQuestionAnswerService {

    // TODO: сверить с реальными значениями QuestionType
    private static final Set<QuestionType> SUPPORTS_OPTIONS =
            EnumSet.of(QuestionType.SINGLE_OPTION, QuestionType.MULTIPLE_OPTION);

    private final VacancyQuestionRepository questionRepository;
    private final VacancyQuestionAnswerRepository answerRepository;
    private final VacancyQuestionAnswerMapper answerMapper;
    private final VacancyQuestionAnswerOptionMapper answerOptionMapper;

    public List<VacancyQuestionAnswerDto> getAllWithAnswers() {
        Integer userId = getCurrentUserId();

        List<VacancyQuestionEntity> questions = questionRepository.findAllWithOptions();
        Map<Integer, VacancyQuestionAnswerEntity> answersByQuestionId =
                answerRepository.findAllByUserIdWithOptions(userId).stream()
                        .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        return questions.stream()
                .map(question -> {
                    VacancyQuestionAnswerEntity answer = answersByQuestionId.get(question.getId());
                    VacancyQuestionAnswerDto dto = answer != null
                            ? answerMapper.fromEntity(answer)
                            : answerMapper.fromQuestion(question);
                    dto.setAnswered(answer != null);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public VacancyQuestionAnswerDto saveAnswer(VacancyQuestionAnswerRequestDto request) {
        Integer userId = getCurrentUserId();

        VacancyQuestionEntity question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new BusinessException(
                        "Вопрос не найден: id=" + request.getQuestionId()));

        validate(question, request);

        VacancyQuestionAnswerEntity answer = answerRepository
                .findByQuestionIdAndUserId(question.getId(), userId)
                .orElseGet(() -> answerMapper.toEntity(question, userId));

        applyValues(question, answer, request);

        VacancyQuestionAnswerEntity saved = answerRepository.save(answer);

        VacancyQuestionAnswerDto dto = answerMapper.fromEntity(saved);
        dto.setAnswered(true);
        return dto;
    }

    private void validate(VacancyQuestionEntity question, VacancyQuestionAnswerRequestDto request) {
        switch (question.getType()) {
            case TEXT -> {
                if (question.isRequired() && !StringUtils.hasText(request.getTextValue())) {
                    throw new BusinessException(
                            "Текстовый ответ обязателен для вопроса id=" + question.getId());
                }
            }
            case YES_NO -> {
                if (question.isRequired() && request.getBoolValue() == null) {
                    throw new BusinessException(
                            "Ответ обязателен для вопроса id=" + question.getId());
                }
            }
            case SINGLE_OPTION, MULTIPLE_OPTION -> {
                List<Integer> selected = request.getSelectedOptionIds();
                boolean empty = selected == null || selected.isEmpty();

                if (empty && question.isRequired()) {
                    throw new BusinessException(
                            "Нужно выбрать вариант ответа для вопроса id=" + question.getId());
                }
                if (!empty) {
                    if (question.getType() == QuestionType.SINGLE_OPTION && selected.size() > 1) {
                        throw new BusinessException(
                                "Для вопроса id=" + question.getId() + " можно выбрать только один вариант");
                    }
                    Set<Integer> validOptionIds = question.getOptions().stream()
                            .map(VacancyQuestionOptionEntity::getId)
                            .collect(Collectors.toSet());
                    if (!validOptionIds.containsAll(selected)) {
                        throw new BusinessException(
                                "Выбран вариант ответа, не относящийся к вопросу id=" + question.getId());
                    }
                }
            }
        }
    }

    private void applyValues(VacancyQuestionEntity question,
                             VacancyQuestionAnswerEntity answer,
                             VacancyQuestionAnswerRequestDto request) {

        answer.setTextValue(question.getType() == QuestionType.TEXT ? request.getTextValue() : null);
        answer.setBoolValue(question.getType() == QuestionType.YES_NO ? request.getBoolValue() : null);

        if (SUPPORTS_OPTIONS.contains(question.getType())) {
            syncSelectedOptions(question, answer, request.getSelectedOptionIds());
        } else {
            answer.getSelectedOptions().clear();
        }
    }

    private void syncSelectedOptions(VacancyQuestionEntity question,
                                     VacancyQuestionAnswerEntity answer,
                                     List<Integer> selectedOptionIds) {
        if (selectedOptionIds == null) {
            selectedOptionIds = List.of();
        }

        Map<Integer, VacancyQuestionOptionEntity> optionsById = question.getOptions().stream()
                .collect(Collectors.toMap(VacancyQuestionOptionEntity::getId, o -> o));

        Set<Integer> newIds = new HashSet<>(selectedOptionIds);

        // убираем то, что сняли
        answer.getSelectedOptions().removeIf(sel -> !newIds.contains(sel.getOption().getId()));

        Set<Integer> existingIds = answer.getSelectedOptions().stream()
                .map(sel -> sel.getOption().getId())
                .collect(Collectors.toSet());

        // добавляем то, чего ещё не было
        for (Integer optionId : newIds) {
            if (!existingIds.contains(optionId)) {
              //  VacancyQuestionAnswerOptionEntity selection = answerOptionMapper.toEntity(answer, optionsById.get(optionId));// vacaVacancyQuestionAnswerOptionEntity.builder()
                ////        .answer(answer)
                    //    .option(optionsById.get(optionId))
                      //  .build();
                answer.getSelectedOptions().add(answerOptionMapper.toEntity(answer, optionsById.get(optionId)));
            }
        }
    }

    public VacancyQuestionAnswerDto getByQuestionId(Integer questionId) {
        Integer userId = getCurrentUserId();

        VacancyQuestionEntity question = questionRepository.findByIdWithOptions(questionId)
                .orElseThrow(() -> new BusinessException("Вопрос не найден: id=" + questionId));

        Optional<VacancyQuestionAnswerEntity> answer =
                answerRepository.findByQuestionIdAndUserId(questionId, userId);

        VacancyQuestionAnswerDto dto = answer
                .map(answerMapper::fromEntity)
                .orElseGet(() -> answerMapper.fromQuestion(question));
        dto.setAnswered(answer.isPresent());
        return dto;
    }

    private Integer getCurrentUserId() {
        return UserUtil.getCurrentUser().getId();
    }
}