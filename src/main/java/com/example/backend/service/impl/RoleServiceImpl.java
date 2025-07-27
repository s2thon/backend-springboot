package com.example.backend.service.impl;

import com.example.backend.entity.Role;
import com.example.backend.repository.RoleRepository;
import com.example.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repo;

    @Override
    public List<Role> findAll() {
        return repo.findAll();
    }

    // ID tipi Long olmalı (JpaRepository<Role, Long>)
    @Override
    public Optional<Role> findById(Long id) {
        return repo.findById(id);
    }

    // RoleRepository’deki metoda karşılık geliyor
    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return repo.findByRoleName(roleName);
    }

    @Override
    public Role save(Role role) {
        return repo.save(role);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
