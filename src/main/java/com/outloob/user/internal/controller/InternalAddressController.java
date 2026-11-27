package com.outloob.user.internal.controller;

import com.outloob.user.address.dto.AddressResponse;
import com.outloob.user.address.service.AddressService;
import com.outloob.user.common.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/users/{userId}/addresses")
@RequiredArgsConstructor
public class InternalAddressController {

    private final AddressService addressService;

    @GetMapping("/{addressId}")
    public ApiResponse<AddressResponse> getAddress(@PathVariable Long userId,
                                                   @PathVariable UUID addressId) {
        return ApiResponse.success(AddressResponse.from(addressService.get(userId, addressId)));
    }
}