package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeFilterForVacancyDto;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.event.ReadyVacancyEvent;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.repository.VacancyRepository;
import ru.kryuch.krtg.searcher.service.EmployeeService;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VacancyJobApplication {

    private final EmployeeService employeeService;
    private final VacancyRepository vacancyRepository;

    @EventListener
    public void handleOrderCreated(ReadyVacancyEvent event) {
        log.info("VacancyJobApplication::handleOrderCreated (event={}", event);
        Optional<VacancyEntity> vacancy = vacancyRepository.findById(event.vacancyId());
        if (vacancy.isPresent()) {
            employeeService.getResumeIdsByFilter(
                EmployeeFilterForVacancyDto.builder()
                    .workFormat(vacancy.get().getWorkFormat())
                    .title(vacancy.get().getTitle())
                    .isFulltime(true)
                    .gradeCodes(vacancy.get().getGrades().stream().map(item -> item.getCode()).collect(Collectors.toSet()))
                    .build()
            );
        }
        else {
            throw new BusinessException(String.format("Bad event for vacancy with id = {}", event.vacancyId()));
        }
    }
}
