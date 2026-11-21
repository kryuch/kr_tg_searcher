package ru.kryuch.krtg.searcher.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileProjectEntity;

import java.util.List;

public interface EmployeeProfileProjectRepository extends JpaRepository<EmployeeProfileProjectEntity, Long> {

    List<EmployeeProfileProjectEntity> findByProfileIdOrderByStartAtDesc(Long profileId);

    @Query("""
        SELECT DISTINCT p FROM EmployeeProfileProjectEntity p
        LEFT JOIN FETCH p.organisation
        LEFT JOIN FETCH p.skills
        WHERE p.profile.id = :profileId
        ORDER BY p.startAt DESC
    """)
    List<EmployeeProfileProjectEntity> findByProfileIdWithDetails(@Param("profileId") Long profileId);
}