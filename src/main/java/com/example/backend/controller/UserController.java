package com.example.backend.controller;

import com.example.backend.dto.UserDto;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.RoleService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> listAll() {
        return userService.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(u -> ResponseEntity.ok(toDto(u)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        User saved = userService.save(toEntity(dto));
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody UserDto dto) {
        return userService.findById(id)
            .map(existing -> {
                User toSave = toEntity(dto);
                toSave.setId(id);
                return ResponseEntity.ok(toDto(userService.save(toSave)));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserDto toDto(User u) {
            UserDto d = new UserDto();
            d.setId(u.getId());
            d.setEmail(u.getEmail());
            d.setFirstName(u.getFirstName());
            d.setLastName(u.getLastName());
            d.setSellerRequested(u.getSellerRequested());
            d.setSellerApproved(u.getSellerApproved());
            return d;


            // u.getId(),
            // u.getEmail(),
            // u.getFirstName(),
            // u.getLastName(),
            // u.getMobileNumber(),
            // u.getActive(),
            // u.getSellerRequested(),
            // u.getRole() != null ? u.getRole().getId() : null
    }
    

    private User toEntity(UserDto d) {
        User u = new User();
        // ID atama isterseniz: u.setId(d.getId());
        u.setEmail(d.getEmail());
        u.setFirstName(d.getFirstName());
        u.setLastName(d.getLastName());
        u.setMobileNumber(d.getMobileNumber());
        // Şifreyi dışarıdan almıyorsanız default geçici şifre; 
        // register endpoint’inde DTO’da password alanı ekleyip encode edin.
        u.setPassword(passwordEncoder.encode("default123")); // veya d.getPassword()
        // Active alanı null gelebilir:
        u.setActive(d.getActive() != null ? d.getActive() : true);
        // Role ataması
        if (d.getRoleId() != null) {
            Role r = roleService.findById(d.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + d.getRoleId()));
            u.setRole(r);
        }
        return u;
    }
}
