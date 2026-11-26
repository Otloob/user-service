package com.outloob.user.address.controller;

import com.outloob.user.address.dto.AddressRequest;
import com.outloob.user.address.dto.AddressResponse;
import com.outloob.user.address.service.AddressService;
import com.outloob.user.common.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        List<AddressResponse> addresses = addressService.list(userId(jwt)).stream()
                .map(AddressResponse::from)
                .toList();
        return ApiResponse.success(addresses);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@AuthenticationPrincipal Jwt jwt,
                                                               @Valid @RequestBody AddressRequest request) {
        var address = addressService.create(userId(jwt), jwt.getClaimAsString("email"), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AddressResponse.from(address), "Address created"));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable UUID id,
                                               @Valid @RequestBody AddressRequest request) {
        var address = addressService.update(userId(jwt), id, request);
        return ApiResponse.success(AddressResponse.from(address), "Address updated");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        addressService.delete(userId(jwt), id);
    }

    @PostMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        var address = addressService.setDefault(userId(jwt), id);
        return ApiResponse.success(AddressResponse.from(address), "Default address updated");
    }

    private static Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}