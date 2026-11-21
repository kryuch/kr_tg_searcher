package ru.kryuch.krtg.searcher.entity.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;

@Entity
@Table(name = "krrg_employee_contacts")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeContactEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "value", nullable = false, length = 500)
    private String value;
}