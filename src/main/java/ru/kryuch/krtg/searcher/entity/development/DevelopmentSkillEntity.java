package ru.kryuch.krtg.searcher.entity.development;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.TimestampedEntity;

@Entity
@Table(name="krrg_development_skills")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DevelopmentSkillEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 16)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 32)
    private String name;

}
