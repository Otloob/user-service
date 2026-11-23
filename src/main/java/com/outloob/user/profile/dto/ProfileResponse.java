package com.outloob.user.profile.dto;

import com.outloob.user.profile.entity.UserProfile;

public record ProfileResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String avatarUrl,
        String locale
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAvatarUrl(),
                profile.getLocale()
        );
    }
}