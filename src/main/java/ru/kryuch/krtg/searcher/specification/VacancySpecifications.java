package ru.kryuch.krtg.searcher.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyFilter;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class VacancySpecifications {

    private VacancySpecifications() {
    }

    public static Specification<VacancyEntity> byFilter(VacancyFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getStatus())) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getWorkFormat() != null) {
                predicates.add(cb.equal(root.get("workFormat"), filter.getWorkFormat()));
            }
            if (filter.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.<LocalDateTime>get("createdAt"),
                    filter.getCreatedFrom().atStartOfDay()));
            }
            if (filter.getCreatedTo() != null) {
                predicates.add(cb.lessThan(
                    root.<LocalDateTime>get("createdAt"),
                    filter.getCreatedTo().plusDays(1).atStartOfDay()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}