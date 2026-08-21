package com.migopan.api.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()){
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }

            if (token != null && jwtService.validarToken(token)){
                String email = jwtService.extrairEmail().orElse(null);
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

                if (usuario != null) {
                    UsernamePasswordAuthentication authentication = new UsernamePasswordAuthenticationToken(
                        usuario, null, null
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            
            filterChain.doFilter(request,response);
    }
}
