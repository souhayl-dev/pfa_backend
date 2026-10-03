package com.bookingapp.api.unit;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.unit.UnitManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** The bookable units of a listing, managed by the owners and managers of its provider. */
@RestController
@RequestMapping("/api/manage")
public class UnitController {

    private final UnitManagementUseCase units;

    public UnitController(UnitManagementUseCase units) {
        this.units = units;
    }

    @PostMapping("/listings/{listingId}/units")
    @ResponseStatus(HttpStatus.CREATED)
    public UnitResponse create(@PathVariable UUID listingId, @Valid @RequestBody UnitRequest request,
                               Authentication authentication) {
        return UnitResponse.from(units.create(CurrentUser.id(authentication), listingId, request.toCommand()));
    }

    @PutMapping("/units/{unitId}")
    public UnitResponse update(@PathVariable UUID unitId, @Valid @RequestBody UnitRequest request,
                               Authentication authentication) {
        return UnitResponse.from(units.update(CurrentUser.id(authentication), unitId, request.toCommand()));
    }

    @PostMapping("/units/{unitId}/activate")
    public UnitResponse activate(@PathVariable UUID unitId, Authentication authentication) {
        return UnitResponse.from(units.activate(CurrentUser.id(authentication), unitId));
    }

    @PostMapping("/units/{unitId}/deactivate")
    public UnitResponse deactivate(@PathVariable UUID unitId, Authentication authentication) {
        return UnitResponse.from(units.deactivate(CurrentUser.id(authentication), unitId));
    }

    @DeleteMapping("/units/{unitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID unitId, Authentication authentication) {
        units.delete(CurrentUser.id(authentication), unitId);
    }
}
