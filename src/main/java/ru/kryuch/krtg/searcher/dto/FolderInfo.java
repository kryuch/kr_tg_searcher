package ru.kryuch.krtg.searcher.dto;

import lombok.Data;
import java.util.Set;

@Data
public class FolderInfo {

    Integer id;
    String title;

    @ToString.Exclude
    Set<Long> chatIds;
}
