package com.outloob.user.address.service;

import com.outloob.user.address.dto.AddressRequest;
import com.outloob.user.address.entity.Address;
import com.outloob.user.address.repository.AddressRepository;
import com.outloob.user.common.exception.ResourceNotFoundException;
import com.outloob.user.profile.service.ProfileService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final ProfileService profileService;

    @Transactional(readOnly = true)
    public List<Address> list(Long userId) {
        return addressRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Address create(Long userId, String email, AddressRequest request) {
        profileService.getOrCreate(userId, email);

        boolean isFirst = addressRepository.countByUserIdAndDeletedAtIsNull(userId) == 0;
        boolean makeDefault = isFirst || Boolean.TRUE.equals(request.isDefault());

        if (makeDefault) {
            clearCurrentDefault(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        copyFields(address, request);
        address.setDefaultAddress(makeDefault);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(Long userId, UUID addressId, AddressRequest request) {
        Address address = findOwned(userId, addressId);
        copyFields(address, request);

        if (Boolean.TRUE.equals(request.isDefault()) && !address.isDefaultAddress()) {
            clearCurrentDefault(userId);
            address.setDefaultAddress(true);
        }
        return address;
    }

    @Transactional
    public void delete(Long userId, UUID addressId) {
        Address address = findOwned(userId, addressId);
        boolean wasDefault = address.isDefaultAddress();

        address.setDeletedAt(Instant.now());
        address.setDefaultAddress(false);
        addressRepository.saveAndFlush(address);

        if (wasDefault) {
            list(userId).stream().findFirst()
                    .ifPresent(next -> next.setDefaultAddress(true));
        }
    }

    @Transactional
    public Address setDefault(Long userId, UUID addressId) {
        Address address = findOwned(userId, addressId);
        if (!address.isDefaultAddress()) {
            clearCurrentDefault(userId);
            address.setDefaultAddress(true);
        }
        return address;
    }

    private Address findOwned(Long userId, UUID addressId) {
        return addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void clearCurrentDefault(Long userId) {
        addressRepository.findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull(userId)
                .ifPresent(old -> {
                    old.setDefaultAddress(false);
                    addressRepository.saveAndFlush(old);
                });
    }

    private void copyFields(Address address, AddressRequest request) {
        address.setLabel(request.label().strip());
        address.setLatitude(request.location().lat());
        address.setLongitude(request.location().lng());
        address.setStreet(request.street().strip());
        address.setBuilding(request.building());
        address.setFloor(request.floor());
        address.setApartment(request.apartment());
        address.setLandmark(request.landmark());
        address.setPhone(request.phone());
    }
}