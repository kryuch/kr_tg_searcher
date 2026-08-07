package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.kryuch.krtg.searcher.entity.FolderChatEntity;
import ru.kryuch.krtg.searcher.entity.FolderChatId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface FolderChatRepository
        extends JpaRepository<FolderChatEntity, FolderChatId> {


    List<FolderChatEntity> findByFolder_IdIn(List<Integer> folderIds);

    @Modifying
    @Query(value = "DELETE FROM krtg_folder_chat WHERE folder_id IN (SELECT id FROM krrg_folders WHERE tg_id = :tgId)", nativeQuery = true)
    int deleteByTgIdNative(@Param("tgId") Integer tgId);

    @Modifying
    @Query("DELETE FROM FolderChatEntity f WHERE f.folder.id IN :folderIds")
    void deleteByFolderIdIn(@Param("folderIds") List<Integer> folderIds);

    List<FolderChatEntity> findByChatUserIdIn(List<Long> chatUserIds);

    @Query("SELECT f.chatUser.id FROM FolderChatEntity f WHERE f.folder.id = :folderId AND f.chatUser.id IN :chatUserIds")
    List<Long> findExistingChatUserIdsByFolderIdAndChatUserIds(
            @Param("folderId") Integer folderId,
            @Param("chatUserIds") List<Long> chatUserIds
    );

    default Map<Long, Boolean> existsByFolderIdAndChatUserIds(Integer folderId, List<Long> chatUserIds) {
        if (chatUserIds == null || chatUserIds.isEmpty()) {
            return new HashMap<>();
        }

        Set<Long> existingChatUserIds = findExistingChatUserIdsByFolderIdAndChatUserIds(folderId, chatUserIds)
                .stream()
                .collect(Collectors.toSet());

        Map<Long, Boolean> result = new HashMap<>();
        for (Long chatUserId : chatUserIds) {
            result.put(chatUserId, existingChatUserIds.contains(chatUserId));
        }
        return result;
    }

    default Map<Long, List<FolderChatEntity>> findByChatUserIdsGrouped(List<Long> chatUserIds) {
        if (CollectionUtils.isEmpty(chatUserIds)) {
            return new HashMap<>();
        }
        return findByChatUserIdIn(chatUserIds)
                .stream()
                .collect(Collectors.groupingBy(
                        entity -> entity.getChatUserId()
                ));
    }

    @Modifying
    @Query("DELETE FROM FolderChatEntity f WHERE f.folder.id = :folderId")
    void deleteByFolderId(@Param("folderId") Integer folderId);

    @Modifying
    @Query("DELETE FROM FolderChatEntity f WHERE f.chatUser.id = :chatUserId")
    void deleteByChatUserId(@Param("chatUserId") Long chatUserId);

  //  boolean existsByFolder_IdAndChat_Id(Integer folderId, Long chatId);
}