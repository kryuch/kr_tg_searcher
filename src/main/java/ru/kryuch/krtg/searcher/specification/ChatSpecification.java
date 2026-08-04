package ru.kryuch.krtg.searcher.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.TgUserEntity;
import ru.kryuch.krtg.searcher.projection.ChatKeyProjection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatSpecification {

    /**
     * Возвращает спецификацию для фильтрации по списку ключей (userId, tgAccountId).
     * Возвращает чаты, которые соответствуют любой из переданных пар.
     */
    public static Specification<ChatEntity> hasKeys(List<ChatKeyProjection> keys) {
        return (root, query, cb) -> {
            if (keys == null || keys.isEmpty()) {
                return cb.conjunction();  // true — без фильтрации
            }

            // Создаём OR-условия для каждой пары
            List<Predicate> predicates = new ArrayList<>();

            for (ChatKeyProjection key : keys) {
                Long userId = key.getUserId();
                Integer tgAccountId = key.getTgAccountId();

                if (userId != null && tgAccountId != null) {
                    // Получаем доступ к полям через JOIN (если user — это связь)
                    Join<ChatEntity, TgUserEntity> userJoin = root.join("user");

                    Predicate userIdPredicate = cb.equal(userJoin.get("id"), userId);
                    Predicate tgIdPredicate = cb.equal(root.get("tgId"), tgAccountId);

                    // Объединяем условия для одной пары через AND
                    Predicate pairPredicate = cb.and(userIdPredicate, tgIdPredicate);
                    predicates.add(pairPredicate);
                }
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            // Объединяем все пары через OR
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Возвращает спецификацию для фильтрации по списку статусов.
     */
    public static Specification<ChatEntity> statusesIn(List<Integer> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Возвращает спецификацию для фильтрации по списку tgId.
     */
    public static Specification<ChatEntity> tgIdsIn(List<Integer> tgIds) {
        return (root, query, cb) -> {
            if (tgIds == null || tgIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("tgId").in(tgIds);
        };
    }

    /**
     * Возвращает спецификацию для фильтрации по списку userId (через связь user).
     */
    public static Specification<ChatEntity> userIdsIn(List<Long> userIds) {
        return (root, query, cb) -> {
            if (userIds == null || userIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("user").get("id").in(userIds);
        };
    }
}