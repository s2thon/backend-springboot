package com.example.backend.service.impl;

import com.example.backend.service.AiIntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import org.slf4j.Logger; // <-- Logger'ı import et
import org.slf4j.LoggerFactory; // <-- LoggerFactory'yi import et
import java.time.Duration;

@Service
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationServiceImpl.class);

    public AiIntegrationServiceImpl(
            WebClient.Builder webClientBuilder, // WebClient yerine WebClient.Builder enjekte edelim
            @Value("${fast-api.base-url}") String fastApiBaseUrl) {
        logger.info("FastAPI Base URL'i şu şekilde ayarlandı: {}", fastApiBaseUrl); // BAŞLANGIÇ LOGU
        this.webClient = webClientBuilder
                .baseUrl(fastApiBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<String> forwardRequest(String path, Object requestBody, String jwtToken) {
        logger.info("FastAPI'ye istek yönlendiriliyor. Path: {}", path);

        return this.webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .doOnSuccess(response -> {
                    // Başarılı ama boş yanıtları yakalamak için log
                    if (response == null || response.trim().isEmpty()) {
                        logger.warn("FastAPI'den başarılı (2xx) ama BOŞ bir yanıt alındı. Path: {}", path);
                    } else {
                        logger.info("FastAPI'den başarılı ve dolu bir yanıt alındı. Path: {}", path);
                    }
                })
                .onErrorResume(error -> {
                    // HATAYI DETAYLI LOGLA VE ANLAMLI BİR JSON DÖNDÜR
                    String errorMessage;
                    if (error instanceof WebClientRequestException) {
                        WebClientRequestException ex = (WebClientRequestException) error;
                        // Bu, "UnknownHostException" gibi ağ hatalarını yakalar
                        errorMessage = "FastAPI servisine ulaşılamıyor (Ağ Hatası). Adres: " + ex.getUri() + ". Detay: "
                                + ex.getMostSpecificCause().getMessage();
                    } else if (error instanceof WebClientResponseException) {
                        // Bu, FastAPI'den gelen 4xx veya 5xx hatalarını yakalar
                        WebClientResponseException ex = (WebClientResponseException) error;
                        errorMessage = "FastAPI servisi bir hata kodu döndürdü. Status: " + ex.getRawStatusCode()
                                + ", Body: " + ex.getResponseBodyAsString();
                    } else {
                        // Diğer tüm hatalar (timeout vb.)
                        errorMessage = "FastAPI ile iletişimde bilinmeyen bir hata oluştu: " + error.getMessage();
                    }

                    logger.error("!!! FastAPI İSTEK HATASI !!! Path: {}. Hata: {}", path, errorMessage, error);

                    // Angular'a her zaman geçerli bir JSON döndür
                    String errorJson = "{\"output\": \"" + errorMessage.replace("\"", "'") + "\", \"suggestions\": []}";
                    return Mono.just(errorJson);
                });
    }
}