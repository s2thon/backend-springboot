package com.example.backend.config;

import com.example.backend.security.JwtAuthenticationFilter;
import com.example.backend.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // 1) OPTIONS her yere açık
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2) Auth işlemleri (login/register) açık
                .requestMatchers("/api/auth/**").permitAll()

                // 3) Public GET’ler (ürün, kategori, yorum)
                .requestMatchers(HttpMethod.GET,
                                 "/api/products/**",
                                 "/api/categories/**",
                                 "/api/products/*/reviews")
                  .permitAll()

                // 4) Ödeme endpoint’i (kullanıcı logged in olmalı)
                .requestMatchers(HttpMethod.POST, "/api/payments/**")
                  .authenticated()

                // 5) Sipariş oluşturma (checkout sonrası)
                .requestMatchers(HttpMethod.POST, "/api/orders/create-after-checkout")
                  .authenticated()

                // 6) Satıcı onay endpoint’i → yalnızca SATI­C­I rolü
                .requestMatchers(HttpMethod.PUT, "/api/orders/seller/orders/**")
                  .hasRole("SELLER")

                // 7) Tüm diğer /api/orders/** istekleri → authenticated
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "SELLER", "ADMIN")

                // Seller API erişimleri
                .requestMatchers("/api/seller/**").hasRole("SELLER")

                // Satıcı sipariş işlemleri
                .requestMatchers("/api/orders/seller/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/approve").hasRole("SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/reject").hasAnyRole("USER", "SELLER", "ADMIN")

                // 8) Rol bazlı genel kurallar
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**")
                  .hasAnyRole("USER","ADMIN","SELLER")

                // Admin endpoints
                .requestMatchers("/api/admin/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/orders/**").hasRole("ADMIN") // Açıkça izin ver

                // 9) Geri kalan tüm istekler authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Angular port
        cfg.addAllowedOrigin("http://localhost:4200");
        // Tüm metodlar (GET, POST, OPTIONS…)
        cfg.addAllowedMethod("*");
        // Authorization header’ına izin ver!
        cfg.addAllowedHeader("*");
        // Gerekirse çerez/credential
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Bu bean sayesinde AuthController’da AuthenticationManager inject edilebilir.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
