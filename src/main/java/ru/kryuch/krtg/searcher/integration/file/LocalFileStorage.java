package ru.kryuch.krtg.searcher.integration.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.kryuch.krtg.searcher.dto.UserFileDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileStorage {

    @Value("${app.file-storage.path}")
    private String root;

    public UserFileDto store(Integer userId, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("У файла отсутствует имя");
        }

        String extension = getExtension(originalName);

        String storageName = UUID.randomUUID() + extension;

        Path userDirectory = getUserDirectory(userId);

        Files.createDirectories(userDirectory);

        Path target = userDirectory.resolve(storageName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return UserFileDto.builder()
                .storageName(storageName)
                .originalName(originalName)
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();
    }

    public InputStream load(Integer userId, String storageName) throws IOException {

        Path path = getPath(userId, storageName);

        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + storageName);
        }

        return Files.newInputStream(path);
    }

    public void delete(Integer userId, String storageName) throws IOException {

        Path path = getPath(userId, storageName);
        Files.deleteIfExists(path);
    }

    public Path getPath(Integer userId, String storageName) {

        validateStorageName(storageName);
        return getUserDirectory(userId).resolve(storageName);
    }

    private Path getUserDirectory(Integer userId) {
        return Path.of(root)
                .resolve(String.valueOf(userId))
                .normalize();
    }

    private String getExtension(String filename) {

        int index = filename.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return filename.substring(index).toLowerCase();
    }

    private void validateStorageName(String storageName) {

        Path path = Path.of(storageName);

        if (path.isAbsolute() || path.getNameCount() != 1) {
            throw new IllegalArgumentException("Некорректное имя файла");
        }

        if (storageName.contains("..")) {
            throw new IllegalArgumentException("Некорректное имя файла");
        }
    }
}
