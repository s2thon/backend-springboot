package com.example.backend.service.impl;

import com.example.backend.service.AiIntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import org.slf4j.Logger; // <-- Logger'ı import et
import org.slf4j.LoggerFactory; // <-- LoggerFactory'yi import et

@Service // Spring'in bu sınıfı bir Bean olarak tanımasını sağlar
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationServiceImpl.class);

    // application.properties dosyasındaki değeri buraya enjekte ediyoruz.
    public AiIntegrationServiceImpl(
            WebClient webClient,
            @Value("${fast-api.base-url}") String fastApiBaseUrl
    ) {
        this.webClient = webClient
                .mutate()
                .baseUrl(fastApiBaseUrl) // Değeri özellikler dosyasından al
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    } 

    @Override // AiIntegrationService arayüzündeki metodu uyguladığımızı belirtir
    public Mono<String> forwardRequest(String path, Object requestBody, String jwtToken) {
        // Hata ayıklama için isteği loglayabiliriz (isteğe bağlı)
        // System.out.println("Forwarding request to: " + path + " with token: " + jwtToken);

        logger.info("Forwarding request to FastAPI path: {}", path);

        return this.webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, jwtToken) // JWT'yi başlığa ekle
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class) // Yanıtı String olarak al

                .timeout(Duration.ofSeconds(60))

                .switchIfEmpty(Mono.just("{\"output\": \"AI servisinden boş bir yanıt alındı. Lütfen tekrar deneyin.\", \"suggestions\": []}"))
                
                // 2. Eğer bir hata olursa (timeout, 500 hatası vb.), bunu logla
                //    ve yine geçerli bir hata JSON'u döndür.
                .doOnError(error -> logger.error("FastAPI isteği başarısız oldu. Path: {}, Hata: {}", path, error.getMessage()))
                .onErrorResume(error -> Mono.just("{\"output\": \"AI servisi şu anda mevcut değil veya bir hata oluştu. Lütfen daha sonra tekrar deneyin.\", \"suggestions\": []}"));
    }
}
