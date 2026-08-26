package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.UsuarioRequestDTO;
import com.migopan.api.dto.UsuarioResponseDTO;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario>
    }

}