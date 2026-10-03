package com.bookingapp.application.shared.port;

import java.io.InputStream;

public interface FileStorage {
    /** Stores the file and returns a URL path the frontend can load it from. */
    String store(String originalFilename, String contentType, InputStream content);
}
