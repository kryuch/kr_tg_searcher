package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.vacancy.ExtensionTaskEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "krrg_vacancies")
@Getter
@Setter
@NoArgsConstructor
public class VacancyEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalId;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(length = 1000)
    private String title;

    @Lob
    private String description;

    @OneToMany(
            mappedBy = "vacancy",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExtensionTaskEntity> extensionTasks = new ArrayList<>();

}
