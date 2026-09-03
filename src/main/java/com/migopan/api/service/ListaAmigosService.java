package com.migopan.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.UsuarioDTOs.*;
import com.migopan.api.dto.ListaAmigosDTOs.*;
import com.migopan.api.dto.GrupoDTOs.*;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.ListaAmigos;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.keys.GrupoMembroId;
import com.migopan.api.model.keys.ListaAmigosId;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.ListaAmigosRepository;
import com.migopan.api.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class ListaAmigosService{
    @Autowired
    private ListaAmigosRepository listaAmigosRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<PerfilResponseDTO> pesquisarUsuariosParaAdicionar(String nome, Long usuarioLogadoId){
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCaseAndIdNot(nome, usuarioLogadoId);
        return usuarios.stream().map(PerfilResponseDTO::new).toList();
    };

    @Transactional 
    public void enviarSolicitacao(Long usuarioId, Long amigoId){
        if (usuarioId.equals(amigoId)) {
            throw new IllegalArgumentException("Você não pode enviar uma solicitação de amizade para si mesmo.");
        }
    
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        
        Usuario amigo = usuarioRepository.findById(amigoId)
                .orElseThrow(() -> new NotFoundException("Amigo não encontrado."));
        
        ListaAmigosId id = new ListaAmigosId(usuarioId, amigoId);
    
        if (listaAmigosRepository.existsById(id)) {
            throw new IllegalArgumentException("Já existe uma solicitação ou amizade entre estes usuários.");
        }

        ListaAmigos relacao = new ListaAmigos();
        relacao.setId(id);
        relacao.setUsuario(usuario);
        relacao.setAmigo(amigo);
        relacao.setStatusAmizade("PENDENTE");
        relacao.setDataAmizade(LocalDateTime.now());

        listaAmigosRepository.save(relacao);
    }

    @Transactional
    public void atualizarStatusAmizade(Long usuarioId, Long amigoId, String novoStatus) {
        ListaAmigosId id = new ListaAmigosId(amigoId, usuarioId);
        ListaAmigos relacao = listaAmigosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitação de amizade não encontrada."));
        
        if (!"ACEITA".equals(novoStatus) && !"RECUSADA".equals(novoStatus)) {
            throw new IllegalArgumentException("Status inválido.");
        }

        relacao.setStatusAmizade(novoStatus);
        listaAmigosRepository.save(relacao);
    }

    public List<AmizadeResponseDTO> listarAmigos(Long usuarioId) {
        return listaAmigosRepository.findAmizadesAceitas(usuarioId);
    }

    public List<AmizadeResponseDTO> listarSolicitacoesRecebidas(Long usuarioId) {
        return listaAmigosRepository.findSolicitacoesPendentesRecebidas(usuarioId);
    }

    public List<AmizadeResponseDTO> listarSolicitacoesEnviadas(Long usuarioId) {
        return listaAmigosRepository.findSolicitacoesPendentesEnviadas(usuarioId);
    }

    @Transactional 
    public void removerAmizade(Long usuarioId, Long amigoId){
        ListaAmigosId id1 = new ListaAmigosId(usuarioId, amigoId);
        ListaAmigosId id2 = new ListaAmigosId(amigoId, usuarioId);

        if (listaAmigosRepository.existsById(id1)) {
            listaAmigosRepository.deleteById(id1);
        } else if (listaAmigosRepository.existsById(id2)) {
            listaAmigosRepository.deleteById(id2);
        } else {
            throw new NotFoundException("Relação de amizade não encontrada.");
        }
    }

}