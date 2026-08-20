package com.migopan.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.security.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) Throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, usuarioRepository);

        http 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(sessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios", "/api/auth/login").permitAll()
                .anyRequest().authenticated() // Exige estar logado
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationToken.class);

        return http.build();
    } 

}
