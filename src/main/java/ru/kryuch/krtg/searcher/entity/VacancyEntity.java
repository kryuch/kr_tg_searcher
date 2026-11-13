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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyCategoryEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyGradeEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancySourceEntity;
import ru.kryuch.krtg.searcher.type.vacancy.VacancyStatus;
import ru.kryuch.krtg.searcher.type.vacancy.WorkFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "krrg_vacancies",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_vacancies__external_id_source",
        columnNames = {"external_id", "source_id"}
    )
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

    @Column(length = 1000, nullable = false)
    private String url;

    @Column(length = 1000, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private VacancySourceEntity source;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_organisation_id", nullable = false)
    private VacancyOwnerOrganisationEntity ownerOrganisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private VacancyCategoryEntity category;

    @BatchSize(size = 25)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "krrg_vacancy_grade_link",
        joinColumns = @JoinColumn(
            name = "vacancy_id",
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "grade_id",
            nullable = false
        ),
        uniqueConstraints = @UniqueConstraint(
            name = "uk_vacancy_grade",
            columnNames = {"vacancy_id", "grade_id"}
        )
    )
    private Set<VacancyGradeEntity> grades = new HashSet<>();

    @Column(name = "rate_resident", precision = 12, scale = 2)
    private BigDecimal rateResident;

    @Column(name = "rate_non_resident", precision = 12, scale = 2)
    private BigDecimal rateNonResident;

    @Size(min = 3, max = 3)
    @Column(name = "rate_currency", length = 3)
    private String rateCurrency;

    @Column(name = "rate_unit", length = 20)
    private String rateUnit;

    @Column(name = "specialists_count")
    private Integer specialistsCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_format", length = 20)
    private WorkFormat workFormat;

    @Column(length = 1000)
    private String location;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(nullable = false)
    private boolean indefinite;

    @Column(length = 255)
    private String citizenship;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "duration_text", length = 500)
    private String durationText;

    @Column(name = "rate_open", nullable = false)
    private boolean rateOpen;

    @Column(name = "raw_text", columnDefinition = "text")
    private String rawText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16)
    private VacancyStatus status = VacancyStatus.NEW;;

    @Column(name = "request_status", length = 50)
    private String requestStatus;

    @Column(name = "actual_on")
    private LocalDate actualOn;

    @Column(nullable = false)
    private boolean extendable;

    @Column(name = "workload_level", length = 50)
    private String workloadLevel;

    @Column(name = "workload_hours_per_month")
    private Integer workloadHoursPerMonth;

    @Column(name = "availability_utc_offset")
    private Integer availabilityUtcOffset;

    @Column(name = "client_payment_terms", columnDefinition = "text")
    private String clientPaymentTerms;

    @Column(name = "client_reviews_count")
    private Integer clientReviewsCount;

    @Column(columnDefinition = "text")
    private String requirements;

    @Column(name = "additional_requirements", columnDefinition = "text")
    private String additionalRequirements;

    @Column(name = "selection_stages", columnDefinition = "text")
    private String selectionStages;

    @Column(name = "joining_conditions", columnDefinition = "text")
    private String joiningConditions;

    @Column(name = "details_loaded", nullable = false)
    private boolean detailsLoaded;
}