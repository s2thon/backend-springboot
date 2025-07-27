package com.example.backend.service;

import com.example.backend.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Optional<Role> findById(Long id);
    Optional<Role> findByRoleName(String roleName);
    Role save(Role role);
    void deleteById(Long id);
}
