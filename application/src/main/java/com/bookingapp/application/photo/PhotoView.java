package com.bookingapp.application.photo;

import com.bookingapp.domain.photo.Photo;

import java.util.UUID;

public record PhotoView(UUID id, String url, int sortOrder) {

    public static PhotoView from(Photo photo) {
        return new PhotoView(photo.id(), photo.url(), photo.sortOrder());
    }
}
