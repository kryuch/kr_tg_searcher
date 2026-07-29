package ru.kryuch.krtg.searcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
    @Query("DELETE FROM FolderChatEntity f WHERE f.folder.id IN :folderIds")
    void deleteByFolderIdIn(@Param("folderIds") List<Integer> folderIds);

    List<FolderChatEntity> findByChat_IdIn(List<Long> chatIds);

    @Query("SELECT f.chat.id FROM FolderChatEntity f WHERE f.folder.id = :folderId AND f.chat.id IN :chatIds")
    List<Long> findExistingChatIdsByFolderIdAndChatIds(
            @Param("folderId") Integer folderId,
            @Param("chatIds") List<Long> chatIds
    );

    default Map<Long, Boolean> existsByFolderIdAndChatIds(Integer folderId, List<Long> chatIds) {
        if (chatIds == null || chatIds.isEmpty()) {
            return new HashMap<>();
        }

        Set<Long> existingChatIds = findExistingChatIdsByFolderIdAndChatIds(folderId, chatIds)
                .stream()
                .collect(Collectors.toSet());

        Map<Long, Boolean> result = new HashMap<>();
        for (Long chatId : chatIds) {
            result.put(chatId, existingChatIds.contains(chatId));
        }
        return result;
    }

    default Map<Long, List<FolderChatEntity>> findByChatIdsGrouped(List<Long> chatIds) {
        if (chatIds == null || chatIds.isEmpty()) {
            return new HashMap<>();
        }
        return findByChat_IdIn(chatIds)
                .stream()
                .collect(Collectors.groupingBy(
                        entity -> entity.getChat().getId()
                ));
    }

    @Modifying
    @Query("DELETE FROM FolderChatEntity f WHERE f.folder.id = :folderId")
    void deleteByFolderId(@Param("folderId") Integer folderId);

    @Modifying
    @Query("DELETE FROM FolderChatEntity f WHERE f.chat.id = :chatId")
    void deleteByChatId(@Param("chatId") Long chatId);

    boolean existsByFolder_IdAndChat_Id(Integer folderId, Long chatId);
}