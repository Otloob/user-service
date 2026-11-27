package com.outloob.user.internal.controller;

import com.outloob.user.common.ApiResponse;
import com.outloob.user.internal.dto.UserSummaryResponse;
import com.outloob.user.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ApiResponse<UserSummaryResponse> getUser(@PathVariable Long id) {
        return ApiResponse.success(UserSummaryResponse.from(profileService.getById(id)));
    }
}