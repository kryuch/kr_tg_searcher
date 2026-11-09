package ru.kryuch.krtg.searcher.repository.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    @Query("""
        SELECT          DISTINCT p
        FROM            ProfileEntity p
        JOIN FETCH      p.employee
        JOIN FETCH      p.grade
        LEFT JOIN FETCH p.profileDirections pd
        LEFT JOIN FETCH pd.direction
        LEFT JOIN FETCH p.profileSkills ps
        LEFT JOIN FETCH ps.skill
        LEFT JOIN FETCH p.profileOrganizations po
        LEFT JOIN FETCH po.organization
        LEFT JOIN FETCH p.profileUniversities pu
        LEFT JOIN FETCH pu.university
        LEFT JOIN FETCH p.profileSpecialties psp
        LEFT JOIN FETCH psp.specialty
        LEFT JOIN FETCH p.profileEducationTypes pet
        LEFT JOIN FETCH pet.educationType
        ORDER BY        p.id ASC
    """)
    List<ProfileEntity> findAllByOrderByIdAscWithDetails();
}
