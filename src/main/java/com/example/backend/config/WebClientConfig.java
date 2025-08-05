package com.example.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Maksimum in-memory boyutu belirle (örneğin 10 MB)
        final int size = 10 * 1024 * 1024; // 10 MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        // --- ZAMAN AŞIMI AYARLARI BURADA BAŞLIYOR ---
        HttpClient httpClient = HttpClient.create()
                // Bağlantı kurmak için bekleme süresi (genellikle kısa)
                .responseTimeout(Duration.ofSeconds(60)); // <-- Render'ın 50 saniyelik gecikmesi için 60 saniye bekle

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient)) // <-- Zaman aşımlı HttpClient'ı ekle
                .build();
    }
}