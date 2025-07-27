package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllBySellerRequestedTrueAndSellerApprovedFalse();

    long count(); // Toplam kullanıcı sayısını döndürür
    long countBySellerApprovedTrue(); // Satıcı olarak onaylanmış kullanıcı sayısını döndürür
    long countBySellerRequestedTrueAndSellerApprovedFalse();

    List<User> findByRoleId(Long roleId);
}
