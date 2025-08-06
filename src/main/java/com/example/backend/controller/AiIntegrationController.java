package com.example.backend.controller;

import com.example.backend.service.AiIntegrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import com.example.backend.model.ChatResponse;

/**
 * Spring Boot ve FastAPI arasında bir köprü (proxy) görevi gören merkezi
 * controller.
 * Bu controller, gelen isteklerin kimliğini ve yetkisini doğrular (rol kontrolü
 * yapar),
 * ardından isteği ve kullanıcının JWT'sini FastAPI'deki ilgili yapay zeka
 * servisine iletir.
 */
@RestController
@RequestMapping("/api/ai")
public class AiIntegrationController {

    private final AiIntegrationService aiIntegrationService;
    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationController.class);

    public AiIntegrationController(AiIntegrationService aiIntegrationService) {
        this.aiIntegrationService = aiIntegrationService;
    }

    /**
     * 'USER' rolüne sahip kullanıcılar için sohbet botu grafiğini tetikler.
     *
     * @param body    Kullanıcının girdisini içeren istek gövdesi.
     * @param request Gelen HTTP isteği, JWT'yi almak için kullanılır.
     * @return FastAPI'den gelen sohbet yanıtı.
     */
    @PostMapping("/chat-invoke")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatResponse> invokeChat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        // 1. Giriş parametrelerini kontrol et
    if (body == null || !body.containsKey("message")) {
        logger.warn("Invalid request body received");
        return ResponseEntity.badRequest().body(
            new ChatResponse("Geçersiz istek formatı", new String[]{"Lütfen geçerli bir mesaj gönderin"})
        );
    }

    final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String userMessage = body.get("message");
    
    logger.info("Received chat request from user. Message length: {}", userMessage.length());

    // 2. FastAPI'ye istek gönder
    try {
        Optional<String> fastApiResponseOptional = aiIntegrationService.forwardRequest("/chat-invoke", body, jwtToken)
                .blockOptional(Duration.ofSeconds(30)); // Timeout ekledik

        if (!fastApiResponseOptional.isPresent()) {
            logger.error("Empty response from FastAPI for message: {}", userMessage);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ChatResponse("AI servisi yanıt vermiyor", new String[]{"Daha sonra tekrar deneyin"}));
        }

        String fastApiResponse = fastApiResponseOptional.get();
        logger.debug("Raw FastAPI response: {}", fastApiResponse);

        // 3. JSON validasyonu ve dönüşüm
        ObjectMapper mapper = new ObjectMapper();
        
        // Önce genel JSON yapısını kontrol et
        JsonNode rootNode = mapper.readTree(fastApiResponse);
        
        if (!rootNode.has("output")) {
            logger.error("Missing 'output' field in FastAPI response. Full response: {}", fastApiResponse);
            return ResponseEntity.ok(
                new ChatResponse("AI yanıt formatı geçersiz", new String[]{"Sistem yöneticisine başvurun"})
            );
        }

        // Daha detaylı validasyon
        ChatResponse response;
        try {
            response = mapper.readValue(fastApiResponse, ChatResponse.class);
            
            // Yanıt içeriğini kontrol et
            if (response.getOutput() == null || response.getOutput().trim().isEmpty()) {
                logger.warn("Empty output in otherwise valid response");
                response.setOutput("AI boş yanıt verdi");
            }
            
            // Önerileri kontrol et (opsiyonel)
            if (response.getSuggestions() == null) {
                response.setSuggestions(new String[0]);
            }
            
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing failed. Response: {}", fastApiResponse, e);
            return ResponseEntity.ok(
                new ChatResponse("Teknik bir sorun oluştu", new String[]{"Daha sonra tekrar deneyin"})
            );
        }

        logger.info("Successfully processed response for message length: {}", userMessage.length());
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        logger.error("Unexpected error processing chat request. Message: {}", userMessage, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ChatResponse("Sistem hatası oluştu", new String[]{"Destek ekibine başvurun"}));
    }
}

    /**
     * 'SELLER' rolüne sahip kullanıcılar için ürün açıklaması oluşturur.
     *
     * @param body    Ürün bilgilerini içeren istek gövdesi.
     * @param request Gelen HTTP isteği, JWT'yi almak için kullanılır.
     * @return FastAPI'den gelen oluşturulmuş ürün açıklaması.
     */
    @PostMapping("/generate-description")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> generateDescription(@RequestBody Object body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String fastApiResponse = aiIntegrationService.forwardRequest("/generate-description", body, jwtToken)
                .block();
        return ResponseEntity.ok(fastApiResponse);
    }

    /**
     * 'SELLER' rolüne sahip kullanıcılar için fiyat analizi yapar.
     *
     * @param body    Analiz edilecek ürün ve pazar bilgilerini içeren istek
     *                gövdesi.
     * @param request Gelen HTTP isteği, JWT'yi almak için kullanılır.
     * @return FastAPI'den gelen fiyat analizi sonucu.
     */
    @PostMapping("/analyze-price")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> analyzePrice(@RequestBody Object body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String fastApiResponse = aiIntegrationService.forwardRequest("/analyze-price", body, jwtToken)
                .block();
        return ResponseEntity.ok(fastApiResponse);
    }

    /**
     * 'SELLER' rolüne sahip kullanıcılar için görsel önizlemesi oluşturur.
     *
     * @param body    Görsel oluşturmak için gereken girdileri (prompt vb.) içeren
     *                istek gövdesi.
     * @param request Gelen HTTP isteği, JWT'yi almak için kullanılır.
     * @return FastAPI'den gelen, genellikle base64 formatında bir görsel verisi.
     */
    @PostMapping("/preview-image")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> previewImage(@RequestBody Object body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String fastApiResponse = aiIntegrationService.forwardRequest("/preview-image", body, jwtToken)
                .block();
        return ResponseEntity.ok(fastApiResponse);
    }

    /**
     * 'SELLER' rolüne sahip kullanıcılar için oluşturulan bir görseli kaydeder.
     *
     * @param body    Kaydedilecek görsel verisini (örn: base64) ve dosya adını
     *                içeren istek gövdesi.
     * @param request Gelen HTTP isteği, JWT'yi almak için kullanılır.
     * @return FastAPI'den gelen, görselin kaydedildiği URL veya bir başarı mesajı.
     */
    @PostMapping("/save-image")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> saveImage(@RequestBody Object body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String fastApiResponse = aiIntegrationService.forwardRequest("/save-image", body, jwtToken)
                .block();
        return ResponseEntity.ok(fastApiResponse);
    }
}
