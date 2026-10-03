package com.bookingapp.api.photo;

import com.bookingapp.application.shared.port.FileStorage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Stores a file and returns its URL, which is then attached as a listing, unit or profile photo. */
@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final FileStorage fileStorage;

    public UploadController(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @PostMapping
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorage.store(file.getOriginalFilename(), file.getContentType(),
                    file.getInputStream());
            return new UploadResponse(url);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to read uploaded file", e);
        }
    }
}
