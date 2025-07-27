package com.example.backend.service;

import com.example.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    List<User> getPendingSellerRequests();
    long countAllUsers();
    long countSellers();
    long countPendingSellerRequests();
    List<User> findByRoleId(Long roleId);

}
