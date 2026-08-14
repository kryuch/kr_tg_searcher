package ru.kryuch.krtg.searcher.integration.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpdateFolderRequest {

    private List<FolderChatIdsRequestItem> items;
    private Boolean addOperationFlag;
}
