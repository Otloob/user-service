package com.outloob.user.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.outloob.user.common.exception.ResourceNotFoundException;
import com.outloob.user.profile.dto.UpdateProfileRequest;
import com.outloob.user.profile.entity.UserProfile;
import com.outloob.user.profile.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    private static final Long USER = 1L;

    @Mock
    UserProfileRepository repository;

    @InjectMocks
    ProfileService profileService;

    private static UserProfile profile(String name, String phone, String locale) {
        UserProfile profile = new UserProfile();
        profile.setId(USER);
        profile.setFullName(name);
        profile.setPhone(phone);
        profile.setLocale(locale);
        return profile;
    }

    @Test
    void getOrCreateCreatesAProfileWhenNoneExists() {
        when(repository.findById(USER)).thenReturn(Optional.empty());
        when(repository.save(any(UserProfile.class))).thenAnswer(call -> call.getArgument(0));

        UserProfile created = profileService.getOrCreate(USER, "a@b.com");

        assertThat(created.getId()).isEqualTo(USER);
        assertThat(created.getEmail()).isEqualTo("a@b.com");
        assertThat(created.getLocale()).isEqualTo("en");
        verify(repository).save(any(UserProfile.class));
    }

    @Test
    void getOrCreateReturnsTheExistingProfileWithoutSaving() {
        UserProfile existing = profile("Sara Ali", "+201001234567", "ar");
        when(repository.findById(USER)).thenReturn(Optional.of(existing));

        UserProfile result = profileService.getOrCreate(USER, "a@b.com");

        assertThat(result).isSameAs(existing);
        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void updateChangesOnlyTheFieldsThatWereSent() {
        UserProfile existing = profile("Old Name", "+201000000000", "en");
        when(repository.findById(USER)).thenReturn(Optional.of(existing));

        UserProfile result = profileService.update(USER, "a@b.com",
                new UpdateProfileRequest(null, null, "ar"));

        assertThat(result.getLocale()).isEqualTo("ar");
        assertThat(result.getFullName()).isEqualTo("Old Name");
        assertThat(result.getPhone()).isEqualTo("+201000000000");
    }

    @Test
    void updateTrimsSpacesAroundTheName() {
        UserProfile existing = profile("Old Name", null, "en");
        when(repository.findById(USER)).thenReturn(Optional.of(existing));

        UserProfile result = profileService.update(USER, "a@b.com",
                new UpdateProfileRequest("  Sara Ali  ", null, null));

        assertThat(result.getFullName()).isEqualTo("Sara Ali");
    }

    @Test
    void updateCreatesTheProfileIfItDoesNotExistYet() {
        when(repository.findById(USER)).thenReturn(Optional.empty());
        when(repository.save(any(UserProfile.class))).thenAnswer(call -> call.getArgument(0));

        UserProfile result = profileService.update(USER, "a@b.com",
                new UpdateProfileRequest("Sara Ali", null, null));

        assertThat(result.getFullName()).isEqualTo("Sara Ali");
        verify(repository).save(any(UserProfile.class));
    }

    @Test
    void getByIdThrowsNotFoundForAnUnknownUser() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}