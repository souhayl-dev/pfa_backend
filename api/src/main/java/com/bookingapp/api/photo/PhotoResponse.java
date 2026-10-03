package com.bookingapp.api.photo;

import com.bookingapp.application.photo.PhotoView;

import java.util.UUID;

public record PhotoResponse(UUID id, String url, int sortOrder) {

    public static PhotoResponse from(PhotoView view) {
        return new PhotoResponse(view.id(), view.url(), view.sortOrder());
    }
}
