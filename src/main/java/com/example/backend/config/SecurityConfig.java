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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity 
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // YETKİLENDİRME KURALLARINI YENİDEN DÜZENLİYORUZ:
            .authorizeHttpRequests(auth -> auth
                // =====================================================================
                // ADIM 1: HERKESE AÇIK YOLLAR (permitAll)
                // Kimlik doğrulaması gerektirmeyen tüm endpoint'ler.
                // =====================================================================
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/*/reviews").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS istekleri genellikle public olmalıdır.

                // =====================================================================
                // ADIM 2: ROL BAZLI ÖZEL KURALLAR
                // Belirli bir role sahip olmayı gerektiren EN ÖZEL yollar.
                // En spesifik olanlar en başa yazılır.
                // =====================================================================

                 // 2. EN SPESİFİK ROL BAZLI KURALLAR (AI Controller için olanlar dahil)
            .requestMatchers("/api/ai/generate-description", 
                             "/api/ai/analyze-price", 
                             "/api/ai/preview-image", 
                             "/api/ai/save-image").hasRole("SELLER")
            .requestMatchers("/api/ai/chat-invoke").hasRole("USER")



                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/seller/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/seller/orders/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/approve").hasRole("SELLER")

                // =====================================================================
                // ADIM 3: BİRDEN FAZLA ROLÜN ERİŞEBİLECEĞİ YOLLAR
                // =====================================================================
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "SELLER", "ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/reject").hasAnyRole("USER", "SELLER", "ADMIN")

                // =====================================================================
                // ADIM 4: GERİYE KALAN TÜM YOLLAR
                // Yukarıdaki kuralların hiçbirine uymayan diğer tüm istekler için
                // sadece kimlik doğrulaması (geçerli bir token) yeterlidir.
                // /api/ai/**, /api/payments/** gibi yollar bu kurala takılacaktır.
                // Rol kontrolü ise bu yolların Controller'larındaki @PreAuthorize'a bırakılmıştır.
                // =====================================================================
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Angular port
        cfg.addAllowedOrigin("http://localhost:4200");

        cfg.addAllowedOrigin("http://localhost:8001"); 

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
