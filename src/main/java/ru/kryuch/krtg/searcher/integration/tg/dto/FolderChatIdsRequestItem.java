package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderChatIdsRequestItem {

    Integer folderId;
    Long id;
    Integer tgAccountId;
}
