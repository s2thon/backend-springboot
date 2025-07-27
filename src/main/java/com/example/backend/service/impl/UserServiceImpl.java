package com.example.backend.service.impl;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<User> getPendingSellerRequests() {
        return repo.findAllBySellerRequestedTrueAndSellerApprovedFalse();
    }

    // eni eklenen metodlar
    @Override
    public long countAllUsers() {
        return repo.count();
    }

    @Override
    public long countSellers() {
        return repo.countBySellerApprovedTrue();
    }

    @Override
    public long countPendingSellerRequests() {
        return repo.countBySellerRequestedTrueAndSellerApprovedFalse();
    }

    @Override
    public List<User> findByRoleId(Long roleId) {
        return repo.findByRoleId(roleId);
    }
}
