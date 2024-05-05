package cz.cvut.fel.ear.reservation_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.ear.reservation_system.security.AuthenticationFailure;
import cz.cvut.fel.ear.reservation_system.security.AuthenticationSuccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebSecurity // Allows Spring Security
@EnableMethodSecurity // Allow methods to be secured using annotation @PreAuthorize and @PostAuthorize
@EnableWebMvc
@Profile("!test")
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AuthenticationSuccess authSuccess = authenticationSuccess();
        // Allow through everything, it will be dealt with using security annotations on
        // methods
        http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                // Return 401 by default when attempting to access a secured endpoint
                .exceptionHandling(
                        ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                // Enable CORS
                .cors(conf -> conf.configurationSource(corsConfigurationSource()))
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // Use custom success and failure handlers
                .formLogin(fl -> fl.successHandler(authSuccess).failureHandler(authenticationFailureHandler()))
                .logout(lgt -> lgt.logoutSuccessHandler(authSuccess));
        return http.build();
    }

    private AuthenticationFailure authenticationFailureHandler() {
        return new AuthenticationFailure(objectMapper);
    }

    private AuthenticationSuccess authenticationSuccess() {
        return new AuthenticationSuccess(objectMapper);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        // We're allowing all methods from all origins so that the application API is
        // usable also by other clients
        // than just the UI.
        // This behavior can be restricted later.
        CorsConfiguration configuration = new CorsConfiguration();
        // AllowCredentials requires a particular origin configured, * is rejected by
        // the browser
        // configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
