package com.migopan.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.security.JwtAuthenticationFilter;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, usuarioRepository);

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. OBRIGATÓRIO: Libera requisições OPTIONS do CORS pré-flight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 2. Corrigida a rota de criação para aceitar /api/usuarios/criar
                .requestMatchers(HttpMethod.POST, "/api/usuarios/criar").permitAll()
                
                // Se quiser permitir também a rota padrão /api/usuarios caso varie o controller:
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                
                // 3. Demais rotas públicas
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                
                // 4. Exige token/autenticação para todo o restante da API
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll() 
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    } 
}