package com.example.backend.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
  @Value("${stripe.api.secret.key}")
  private String apiSecretKey;

  @Value("${stripe.api.publishable.key}")
  private String apiPublishableKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = apiSecretKey;
  }

  /**
   * Uygulama içinde ihtiyaç duyduğun yerde inject edebileceğin
   * publishable key bean’i. Örneğin bir controller’dan:
   *
   * @GetMapping("/api/stripe/key")
   * public Map<String,String> getKey(@Autowired String stripePublishableKey) {
   *   return Map.of("key", stripePublishableKey);
   * }
   */
  @Bean
  public String stripePublishableKey() {
    return apiPublishableKey;
  }
}

