package com.outloob.user.address.dto;

import com.outloob.user.address.entity.Address;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String label,
        LocationDto location,
        String street,
        String building,
        String floor,
        String apartment,
        String landmark,
        String phone,
        boolean isDefault
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                new LocationDto(address.getLatitude(), address.getLongitude()),
                address.getStreet(),
                address.getBuilding(),
                address.getFloor(),
                address.getApartment(),
                address.getLandmark(),
                address.getPhone(),
                address.isDefaultAddress()
        );
    }
}