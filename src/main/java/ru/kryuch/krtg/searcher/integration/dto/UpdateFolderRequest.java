package ru.kryuch.krtg.searcher.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateFolderRequest {

    private Integer folderId;
    private List<Long> chatIds;
    private Boolean addOperationFlag;
}
