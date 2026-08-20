package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.UserFileDto;
import ru.kryuch.krtg.searcher.integration.file.LocalFileStorage;
import ru.kryuch.krtg.searcher.util.UserUtil;

@Service
@RequiredArgsConstructor
public class UserFileService {

    private final UserFileAccessService userFileAccessService;

    private final LocalFileStorage localFileStorage;

    public void add() {
     //   localFileStorage.
    }

    public void add(UserFileDto dto) {
 //localFileStorage.load(UserUtil.getCurrentUser(), dto.getStorageName());
    }
}
