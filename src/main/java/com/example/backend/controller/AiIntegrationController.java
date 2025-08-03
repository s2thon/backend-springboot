package com.example.backend.controller;

import com.example.backend.service.AiIntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Spring Boot ve FastAPI arasında bir köprü (proxy) görevi gören merkezi controller.
 * Bu controller, gelen isteklerin kimliğini ve yetkisini doğrular (rol kontrolü yapar),
 * ardından isteği ve kullanıcının JWT'sini FastAPI'deki ilgili yapay zeka servisine iletir.
 */
@RestController
@RequestMapping("/api/ai")
public class AiIntegrationController {

    private final AiIntegrationService aiIntegrationService;

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
    public ResponseEntity<String> invokeChat(@RequestBody Object body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String fastApiResponse = aiIntegrationService.forwardRequest("/chat-invoke", body, jwtToken)
                                                     .block(); // <-- EN ÖNEMLİ DEĞİŞİKLİK BURADA

        // Gelen cevabı direkt olarak dön.
        return ResponseEntity.ok(fastApiResponse);
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
     * @param body    Analiz edilecek ürün ve pazar bilgilerini içeren istek gövdesi.
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
     * @param body    Görsel oluşturmak için gereken girdileri (prompt vb.) içeren istek gövdesi.
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
     * @param body    Kaydedilecek görsel verisini (örn: base64) ve dosya adını içeren istek gövdesi.
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
