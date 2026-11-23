package com.outloob.user.profile.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(min = 1, max = 120, message = "must be between 1 and 120 characters")
        String fullName,

        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "must be 8-15 digits, optional leading +")
        String phone,

        @Pattern(regexp = "^(en|ar)$", message = "must be 'en' or 'ar'")
        String locale
) {}