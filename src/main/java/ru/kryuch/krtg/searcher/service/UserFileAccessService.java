package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kryuch.krtg.searcher.dto.UserFileDto;
import ru.kryuch.krtg.searcher.entity.UserFileEntity;
import ru.kryuch.krtg.searcher.integration.file.LocalFileStorage;
import ru.kryuch.krtg.searcher.mapper.UserFileMapper;
import ru.kryuch.krtg.searcher.repository.UserFileRepository;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.io.IOException;

@Service
public class UserFileAccessService extends AbstractAccessService<Integer, UserFileEntity, UserFileDto, UserFileMapper, UserFileRepository> {

    private final LocalFileStorage localFileStorage;

    public UserFileAccessService(UserFileRepository userFileRepository, UserFileMapper userFileMapper, LocalFileStorage localFileStorage) {
        super(userFileRepository, userFileMapper, "файлы");
        this.localFileStorage = localFileStorage;
    }

    public UserFileDto upload(MultipartFile file) throws IOException {
        Integer userId = UserUtil.getCurrentUser().getId();

        UserFileDto userFileDto = localFileStorage.store(userId, file);

        try {
            UserFileEntity userFileEntity = mapper.toEntity(userFileDto);
            userFileEntity.setUserId(userId);
            return mapper.fromEntity(repository.save(userFileEntity));

        } catch (Exception e) {
            localFileStorage.delete(userId, userFileDto.getStorageName());

            throw e;
        }
    }

}
