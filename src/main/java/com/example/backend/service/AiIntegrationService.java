package com.example.backend.service;

import reactor.core.publisher.Mono;

/**
 * Spring Boot ve FastAPI arasında bir köprü görevi gören servis için sözleşme (contract).
 * Bu servis, istekleri FastAPI'deki yapay zeka araçlarına güvenli bir şekilde iletmekle sorumludur.
 */
public interface AiIntegrationService {

    /**
     * Gelen isteği ve JWT'yi FastAPI'deki belirtilen yola iletir.
     *
     * @param path        FastAPI'de hedeflenen yol (örn: "/chat-invoke")
     * @param requestBody FastAPI'ye gönderilecek JSON gövdesi
     * @param jwtToken    Kullanıcının "Bearer " ön ekiyle birlikte tam JWT'si
     * @return FastAPI'den gelen yanıtı içeren bir Mono<String>
     */
    Mono<String> forwardRequest(String path, Object requestBody, String jwtToken);
}