package ru.kryuch.krtg.searcher.entity.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "krrg_employee_development_direction")
@NoArgsConstructor
@Getter
@Setter
public class DevelopmentDirectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    public DevelopmentDirectionEntity(String name) {
        this.name = name;
    }

    public DevelopmentDirectionEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
