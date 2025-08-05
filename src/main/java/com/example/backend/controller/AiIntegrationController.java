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
import java.util.Map;
import java.util.Optional;

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
    // 1. @RequestBody'yi doğrudan bir Map<String, String> olarak alıyoruz.
    //    Bu, {"message": "..."} JSON'uyla birebir uyumludur.
    public ResponseEntity<String> invokeChat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        final String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 1. Servisten gelen sonucu bir Optional'a sararak alıyoruz.
        //    .blockOptional() metodu, eğer Mono boşsa (null'a neden olacaksa),
        //    içi boş bir Optional döndürür. Bu, NullPointerException'ı önler.
        Optional<String> fastApiResponseOptional = aiIntegrationService.forwardRequest("/chat-invoke", body, jwtToken)
                                                                       .blockOptional();

        // 2. Gelen cevabın null veya boş olup olmadığını kontrol ediyoruz.
        //    Eğer boşsa, frontend'in çökmesini önlemek için güvenli bir varsayılan JSON döndürüyoruz.
        String responseBody = fastApiResponseOptional
                .orElse("{\"output\": \"AI servisinden bir hata nedeniyle yanıt alınamadı.\", \"suggestions\": []}");

        // 3. Her durumda geçerli bir JSON string'i içeren cevabı frontend'e gönderiyoruz.
        return ResponseEntity.ok(responseBody);
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
