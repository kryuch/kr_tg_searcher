package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.projection.ChatKeyProjection;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends CrudRepository<ChatEntity, Long>, JpaSpecificationExecutor<ChatEntity> {

    @Query("SELECT c.user.id as userId, c.tgId as tgAccountId FROM ChatEntity c WHERE c.status = :status")
    List<ChatKeyProjection> findKeysByStatusEqual(@Param("status") Integer status);

    @Query("SELECT c.user.id as userId, c.tgId as tgAccountId FROM ChatEntity c WHERE c.status > :status")
    List<ChatKeyProjection> findKeysByStatusGreaterThan(@Param("status") Integer status);

    @Query("SELECT c FROM ChatEntity c WHERE c.tgId = :tgId AND c.user.id IN :userIds")
    List<ChatEntity> findAllByTgIdAndUserIds(@Param("tgId") Integer tgId, @Param("userIds") List<Long> userIds);

    @Query("SELECT c FROM ChatEntity c WHERE c.user.id IN :userIds")
    List<ChatEntity> findAllByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT c FROM ChatEntity c WHERE c.user.id = :userId AND c.tgId = :tgId")
    Optional<ChatEntity> findByUserIdAndTgId(@Param("userId") Long userId, @Param("tgId") Integer tgId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM ChatEntity c WHERE c.id IN :ids")
    List<ChatEntity> findEnrichedByIds(@Param("ids") List<Long> ids);

    /**
     * Возвращает чаты, удовлетворяющие фильтрам.
     *
     * @param statuses список статусов (если null или пустой — не фильтруется)
     * @param tgIds    список ID аккаунтов (если null или пустой — не фильтруется)
     * @return список чатов
     */
    default List<ChatEntity> findEnrichedByStatusesAndTgIdsDynamic(
            List<Integer> statuses,
            List<Integer> tgIds
    ) {
        // Проверяем, что хотя бы один фильтр задан
        boolean hasStatuses = statuses != null && !statuses.isEmpty();
        boolean hasTgIds = tgIds != null && !tgIds.isEmpty();

        if (!hasStatuses && !hasTgIds) {
            // Оба фильтра пустые → возвращаем все
            return findAllEnriched();
        } else if (!hasStatuses) {
            // Только tgIds
            return findByTgIdsEnriched(tgIds);
        } else if (!hasTgIds) {
            // Только statuses
            return findByStatusesEnriched(statuses);
        } else {
            // Оба фильтра активны
            return findEnrichedByStatusesAndTgIds(statuses, tgIds);
        }
    }

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM ChatEntity c WHERE c.tgId IN :tgIds")
    List<ChatEntity> findByTgIdsEnriched(@Param("tgIds") List<Integer> tgIds);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM ChatEntity c WHERE c.status IN :statuses")
    List<ChatEntity> findByStatusesEnriched(@Param("statuses") List<Integer> statuses);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM ChatEntity c")
    List<ChatEntity> findAllEnriched();

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM ChatEntity c WHERE c.status IN :statuses AND c.tgId IN :tgIds")
    List<ChatEntity> findEnrichedByStatusesAndTgIds(
            @Param("statuses") List<Integer> statuses,
            @Param("tgIds") List<Integer> tgIds
    );

    @Query("SELECT c FROM ChatEntity c WHERE c.user.id IN :users AND c.tgId IN :tgIds")
    List<ChatEntity> findByUserIdsAndTgIds(
            @Param("users") List<Long> users,
            @Param("tgIds") List<Integer> tgIds
    );
}