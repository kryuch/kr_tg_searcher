package ru.kryuch.krtg.searcher.repository.vacancy;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyResponseEntity;
import ru.kryuch.krtg.searcher.repository.BaseAccessRepository;

import java.util.List;

public interface VacancyResponseRepository extends BaseAccessRepository<VacancyResponseEntity, Long> {

    @Query("""
            SELECT r FROM VacancyResponseEntity r
            JOIN FETCH r.vacancy v
            JOIN FETCH v.ownerOrganisation
            WHERE r.userId = :userId
            """)
    List<VacancyResponseEntity> findAllByUserIdWithVacancy(@Param("userId") Integer userId);
}