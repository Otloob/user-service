package com.outloob.user.address.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.outloob.user.address.dto.AddressRequest;
import com.outloob.user.address.dto.LocationDto;
import com.outloob.user.address.entity.Address;
import com.outloob.user.address.repository.AddressRepository;
import com.outloob.user.common.exception.ResourceNotFoundException;
import com.outloob.user.profile.service.ProfileService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    private static final Long USER = 1L;

    @Mock
    AddressRepository addressRepository;

    @Mock
    ProfileService profileService;

    @InjectMocks
    AddressService addressService;

    private static AddressRequest request(Boolean isDefault) {
        return new AddressRequest("Home", new LocationDto(30.0, 31.0), "Street 1",
                null, null, null, null, null, isDefault);
    }

    private static Address address(boolean isDefault) {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUserId(USER);
        address.setLabel("Existing");
        address.setDefaultAddress(isDefault);
        return address;
    }

    @Test
    void firstAddressBecomesDefaultEvenIfNotRequested() {
        when(addressRepository.countByUserIdAndDeletedAtIsNull(USER)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address created = addressService.create(USER, "a@b.com", request(false));

        assertThat(created.isDefaultAddress()).isTrue();
    }

    @Test
    void secondAddressDoesNotStealTheDefault() {
        when(addressRepository.countByUserIdAndDeletedAtIsNull(USER)).thenReturn(1L);
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address created = addressService.create(USER, "a@b.com", request(false));

        assertThat(created.isDefaultAddress()).isFalse();
        verify(addressRepository, never()).saveAndFlush(any(Address.class));
    }

    @Test
    void newDefaultDemotesTheOldDefault() {
        Address oldDefault = address(true);
        when(addressRepository.countByUserIdAndDeletedAtIsNull(USER)).thenReturn(1L);
        when(addressRepository.findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull(USER))
                .thenReturn(Optional.of(oldDefault));
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address created = addressService.create(USER, "a@b.com", request(true));

        assertThat(created.isDefaultAddress()).isTrue();
        assertThat(oldDefault.isDefaultAddress()).isFalse();
        verify(addressRepository).saveAndFlush(oldDefault);
    }

    @Test
    void deletingTheDefaultPromotesTheNewestRemainingAddress() {
        Address deleted = address(true);
        Address remaining = address(false);
        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(deleted.getId(), USER))
                .thenReturn(Optional.of(deleted));
        when(addressRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(USER))
                .thenReturn(List.of(remaining));

        addressService.delete(USER, deleted.getId());

        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.isDefaultAddress()).isFalse();
        assertThat(remaining.isDefaultAddress()).isTrue();
    }

    @Test
    void updatingSomeoneElsesAddressIsNotFound() {
        UUID someId = UUID.randomUUID();
        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(someId, USER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.update(USER, someId, request(false)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}