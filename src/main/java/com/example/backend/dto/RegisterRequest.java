package com.example.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String roleName;    // Örn: "ADMIN", "USER" veya "SELLER"
    private String firstName;
    private String lastName;
    private Boolean sellerRequested;
}
