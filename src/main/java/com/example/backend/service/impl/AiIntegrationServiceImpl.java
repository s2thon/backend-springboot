package com.example.backend.service.impl;

import com.example.backend.service.AiIntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service // Spring'in bu sınıfı bir Bean olarak tanımasını sağlar
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final WebClient webClient;

    // application.properties dosyasındaki değeri buraya enjekte ediyoruz.
    public AiIntegrationServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${fast-api.base-url}") String fastApiBaseUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(fastApiBaseUrl) // Değeri özellikler dosyasından al
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    } 

    @Override // AiIntegrationService arayüzündeki metodu uyguladığımızı belirtir
    public Mono<String> forwardRequest(String path, Object requestBody, String jwtToken) {
        // Hata ayıklama için isteği loglayabiliriz (isteğe bağlı)
        // System.out.println("Forwarding request to: " + path + " with token: " + jwtToken);

        return this.webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, jwtToken) // JWT'yi başlığa ekle
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class); // Yanıtı String olarak al
    }
}
