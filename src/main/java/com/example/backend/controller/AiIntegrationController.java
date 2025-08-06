package com.example.backend.controller;

import com.example.backend.service.AiIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        logger.info("Received chat request with message: {}", body.get("message"));

        Optional<String> fastApiResponseOptional = aiIntegrationService.forwardRequest("/chat-invoke", body, jwtToken)
                .blockOptional();

        if (!fastApiResponseOptional.isPresent()) {
            logger.error("Empty response from FastAPI");
            return ResponseEntity.ok(new ChatResponse("AI servisinden yanıt alınamadı", new String[] {}));
        }

        try {
            // JSON'ı parse et ve ChatResponse'ye dönüştür
            ObjectMapper mapper = new ObjectMapper();
            ChatResponse response = mapper.readValue(fastApiResponseOptional.get(), ChatResponse.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to parse FastAPI response: {}", e.getMessage());
            return ResponseEntity.ok(new ChatResponse("Yanıt işlenirken hata oluştu", new String[] {}));
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
