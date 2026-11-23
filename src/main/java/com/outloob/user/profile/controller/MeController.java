package com.outloob.user.profile.controller;

import com.outloob.user.common.ApiResponse;
import com.outloob.user.profile.dto.ProfileResponse;
import com.outloob.user.profile.dto.UpdateProfileRequest;
import com.outloob.user.profile.entity.UserProfile;
import com.outloob.user.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<ProfileResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        UserProfile profile = profileService.getOrCreate(userId(jwt), email(jwt));
        return ApiResponse.success(ProfileResponse.from(profile));
    }

    @PatchMapping
    public ApiResponse<ProfileResponse> updateMe(@AuthenticationPrincipal Jwt jwt,
                                                 @Valid @RequestBody UpdateProfileRequest request) {
        UserProfile profile = profileService.update(userId(jwt), email(jwt), request);
        return ApiResponse.success(ProfileResponse.from(profile), "Profile updated");
    }

    private static Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private static String email(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }
}