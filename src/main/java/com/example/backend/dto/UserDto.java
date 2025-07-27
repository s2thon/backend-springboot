// src/main/java/com/example/backend/dto/UserDto.java
package com.example.backend.dto;

import com.example.backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private Boolean active;
    private Long roleId;
    private Boolean sellerRequested;
    private Boolean sellerApproved;

    // public static UserDto fromEntity(User u) {
    //     UserDto d = new UserDto();
    //     d.setId(u.getId());
    //     d.setEmail(u.getEmail());
    //     d.setFirstName(u.getFirstName());
    //     d.setLastName(u.getLastName());
    //     d.setMobileNumber(u.getMobileNumber());
    //     d.setActive(u.getActive());
    //     d.setRoleId(u.getRole().getId());
    //     d.setSellerRequested(u.getSellerRequested());
    //     d.setSellerApproved(u.getSellerApproved());
    //     return d;
    // }
}
