package ru.kryuch.krtg.searcher.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;

@Repository
public interface EducationTypeRepository extends JpaRepository<EducationTypeEntity, Long> {
}
