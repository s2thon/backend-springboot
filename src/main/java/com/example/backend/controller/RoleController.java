// src/main/java/com/example/backend/controller/RoleController.java
package com.example.backend.controller;

import com.example.backend.dto.RoleDto;
import com.example.backend.entity.Role;
import com.example.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<RoleDto> listAll() {
        return roleService.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getById(@PathVariable Long id) {
        return roleService.findById(id)
            .map(r -> ResponseEntity.ok(toDto(r)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public RoleDto create(@RequestBody RoleDto dto) {
        Role saved = roleService.save(toEntity(dto));
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> update(@PathVariable Long id,
                                          @RequestBody RoleDto dto) {
        return roleService.findById(id)
            .map(existing -> {
                Role toSave = toEntity(dto);
                toSave.setId(id);
                return ResponseEntity.ok(toDto(roleService.save(toSave)));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (roleService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private RoleDto toDto(Role r) {
        return new RoleDto(r.getId(), r.getRoleName());
    }

    private Role toEntity(RoleDto d) {
        Role r = new Role();
        // ID'yi yalnızca güncellemede alıyoruz, create'da null kalır
        r.setRoleName(d.getRoleName());
        return r;
    }
}
