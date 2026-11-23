package com.outloob.user.profile.service;

import com.outloob.user.profile.dto.UpdateProfileRequest;
import com.outloob.user.profile.entity.UserProfile;
import com.outloob.user.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository repository;

    @Transactional
    public UserProfile getOrCreate(Long userId, String email) {
        return repository.findById(userId).orElseGet(() -> {
            UserProfile profile = new UserProfile();
            profile.setId(userId);
            profile.setEmail(email);
            return repository.save(profile);
        });
    }

    @Transactional
    public UserProfile update(Long userId, String email, UpdateProfileRequest request) {
        UserProfile profile = getOrCreate(userId, email);

        if (request.fullName() != null) {
            profile.setFullName(request.fullName().strip());
        }
        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }
        if (request.locale() != null) {
            profile.setLocale(request.locale());
        }
        return profile;
    }
}