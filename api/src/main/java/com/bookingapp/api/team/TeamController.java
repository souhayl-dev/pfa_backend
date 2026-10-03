package com.bookingapp.api.team;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.team.TeamUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** A provider's team. Owners manage everyone; managers can add and remove staff. */
@RestController
@RequestMapping("/api")
public class TeamController {

    private final TeamUseCase team;

    public TeamController(TeamUseCase team) {
        this.team = team;
    }

    @GetMapping("/providers/{providerId}/members")
    public List<MemberResponse> members(@PathVariable UUID providerId, Authentication authentication) {
        return team.members(CurrentUser.id(authentication), providerId).stream().map(MemberResponse::from).toList();
    }

    @PostMapping("/providers/{providerId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse add(@PathVariable UUID providerId, @Valid @RequestBody AddMemberRequest request,
                              Authentication authentication) {
        return MemberResponse.from(
                team.addMember(CurrentUser.id(authentication), providerId, request.email(), request.role()));
    }

    @PatchMapping("/members/{memberId}/role")
    public MemberResponse changeRole(@PathVariable UUID memberId, @Valid @RequestBody ChangeRoleRequest request,
                                     Authentication authentication) {
        return MemberResponse.from(team.changeRole(CurrentUser.id(authentication), memberId, request.role()));
    }

    @PostMapping("/members/{memberId}/suspend")
    public MemberResponse suspend(@PathVariable UUID memberId, Authentication authentication) {
        return MemberResponse.from(team.suspend(CurrentUser.id(authentication), memberId));
    }

    @PostMapping("/members/{memberId}/reactivate")
    public MemberResponse reactivate(@PathVariable UUID memberId, Authentication authentication) {
        return MemberResponse.from(team.reactivate(CurrentUser.id(authentication), memberId));
    }

    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID memberId, Authentication authentication) {
        team.remove(CurrentUser.id(authentication), memberId);
    }
}
