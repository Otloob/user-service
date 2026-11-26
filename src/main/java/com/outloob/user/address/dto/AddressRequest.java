package com.outloob.user.address.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "is required")
        @Size(max = 50, message = "must be at most 50 characters")
        String label,

        @NotNull(message = "is required")
        @Valid
        LocationDto location,

        @NotBlank(message = "is required")
        @Size(max = 255, message = "must be at most 255 characters")
        String street,

        @Size(max = 50, message = "must be at most 50 characters")
        String building,

        @Size(max = 20, message = "must be at most 20 characters")
        String floor,

        @Size(max = 20, message = "must be at most 20 characters")
        String apartment,

        @Size(max = 255, message = "must be at most 255 characters")
        String landmark,

        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "must be 8-15 digits, optional leading +")
        String phone,

        Boolean isDefault
) {}