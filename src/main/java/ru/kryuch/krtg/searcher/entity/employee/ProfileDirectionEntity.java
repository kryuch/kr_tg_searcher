package ru.kryuch.krtg.searcher.entity.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;

@Getter
@Setter
@Entity
@Table(name = "krrg_employee_profile_direction")
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDirectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private EmployeeProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "direction_id", nullable = false)
    private DevelopmentSpecializationEntity direction;
}
