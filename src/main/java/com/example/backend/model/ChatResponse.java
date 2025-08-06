package com.example.backend.model; // Veya uygun bir paket adı

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    @NotBlank
    private String output;

    @NotNull
    private String[] suggestions = new String[0];
}