package com.bookingapp.infrastructure.shared.storage;

import com.bookingapp.application.shared.port.FileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Stores uploaded photos on local disk under `app.upload-dir`, served back via /uploads/** (see WebConfig). */
@Component
public class LocalFileStorage implements FileStorage {

    private final Path uploadDir;

    public LocalFileStorage(@Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("could not create upload directory: " + this.uploadDir, e);
        }
    }

    @Override
    public String store(String originalFilename, String contentType, InputStream content) {
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve(storedFilename);
        try {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to store uploaded file", e);
        }
        return "/uploads/" + storedFilename;
    }

    private static String extractExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        return dotIndex >= 0 ? originalFilename.substring(dotIndex) : "";
    }
}
