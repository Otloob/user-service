package com.outloob.user.internal.dto;

import com.outloob.user.profile.entity.UserProfile;

public record UserSummaryResponse(Long id, String fullName, String phone, String locale) {

    public static UserSummaryResponse from(UserProfile profile) {
        return new UserSummaryResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getLocale()
        );
    }
}