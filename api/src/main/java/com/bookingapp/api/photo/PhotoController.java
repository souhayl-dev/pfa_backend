package com.bookingapp.api.photo;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.photo.PhotoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Photos of listings and units, managed by the owners and managers of the listing's provider. */
@RestController
@RequestMapping("/api/manage")
public class PhotoController {

    private final PhotoUseCase photos;

    public PhotoController(PhotoUseCase photos) {
        this.photos = photos;
    }

    @PostMapping("/listings/{listingId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponse addToListing(@PathVariable UUID listingId, @Valid @RequestBody AddPhotoRequest request,
                                      Authentication authentication) {
        return PhotoResponse.from(photos.addToListing(CurrentUser.id(authentication), listingId, request.url()));
    }

    @PostMapping("/units/{unitId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponse addToUnit(@PathVariable UUID unitId, @Valid @RequestBody AddPhotoRequest request,
                                   Authentication authentication) {
        return PhotoResponse.from(photos.addToUnit(CurrentUser.id(authentication), unitId, request.url()));
    }

    @DeleteMapping("/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID photoId, Authentication authentication) {
        photos.remove(CurrentUser.id(authentication), photoId);
    }
}
