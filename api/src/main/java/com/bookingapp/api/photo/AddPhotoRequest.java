package com.bookingapp.api.photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** url is what POST /api/uploads returned, or any image address. */
public record AddPhotoRequest(@NotBlank @Size(max = 500) String url) {
}
