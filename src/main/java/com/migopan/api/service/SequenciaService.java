package com.migopan.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.TarefaRepository;
import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.util.SequenciaDias;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SequenciaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;

    @Transactional
    public void recalcularUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            var resultado = SequenciaDias.calcular(tarefaRepository.diasComConclusaoDoUsuario(usuarioId));
            usuario.setSequenciaDias(resultado.sequencia());
            usuario.setUltimaAtividade(resultado.ultimoDia());
        });
    }

    @Transactional
    public void recalcularGrupo(Long grupoId) {
        grupoRepository.findById(grupoId).ifPresent(grupo -> {
            var resultado = SequenciaDias.calcular(tarefaRepository.diasComConclusaoDoGrupo(grupoId));
            grupo.setSequenciaDias(resultado.sequencia());
            grupo.setUltimaAtividade(resultado.ultimoDia());
        });
    }
}
