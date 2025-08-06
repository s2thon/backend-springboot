package com.example.backend.model; // Veya uygun bir paket adı

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String output;
    private String[] suggestions;
}