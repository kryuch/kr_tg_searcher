package ru.kryuch.krtg.searcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.type.VacancySource;

@Entity
@Table(
        name = "krrg_vacancies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"external_id", "source"})
)
@Getter
@Setter
@NoArgsConstructor
public class VacancyEntity extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(length = 1000)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private VacancySource source;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_organisation_id", nullable = false)
    private VacancyOwnerOrganisationEntity ownerOrganisation;
}