package com.outloob.user.address.repository;

import com.outloob.user.address.entity.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    Optional<Address> findByIdAndUserIdAndDeletedAtIsNull(UUID id, Long userId);

    Optional<Address> findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull(Long userId);

    long countByUserIdAndDeletedAtIsNull(Long userId);
}