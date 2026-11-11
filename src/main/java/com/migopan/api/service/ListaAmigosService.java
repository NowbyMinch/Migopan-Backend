package com.migopan.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.ListaAmigosDTOs.AmizadeResponseDTO;
import com.migopan.api.dto.UsuarioDTOs.PerfilResponseDTO;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.ListaAmigos;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.enums.StatusAmizade;
import com.migopan.api.model.keys.ListaAmigosId;
import com.migopan.api.repository.ListaAmigosRepository;
import com.migopan.api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListaAmigosService {

    private final ListaAmigosRepository listaAmigosRepository;
    private final UsuarioRepository usuarioRepository;

    public List<PerfilResponseDTO> pesquisarUsuariosParaAdicionar(String nome, Long usuarioLogadoId) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe um nome para pesquisar.");
        }
        return usuarioRepository.buscarPorNomeExcluindoUsuarioLogado(nome.trim(), usuarioLogadoId).stream()
                .map(PerfilResponseDTO::new)
                .toList();
    }

    @Transactional
    public void enviarSolicitacao(Long usuarioId, Long amigoId) {
        if (usuarioId.equals(amigoId)) {
            throw new IllegalArgumentException("Você não pode enviar uma solicitação de amizade para si mesmo.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        Usuario amigo = usuarioRepository.findById(amigoId)
                .orElseThrow(() -> new NotFoundException("Amigo não encontrado."));

        // Confere os DOIS sentidos: se o outro já me enviou, não cria uma segunda linha
        List<ListaAmigos> existentes = listaAmigosRepository.findRelacaoEntre(usuarioId, amigoId);
        if (existentes.stream().anyMatch(r -> r.getStatusAmizade() != StatusAmizade.RECUSADA)) {
            throw new IllegalArgumentException("Já existe uma solicitação ou amizade entre estes usuários.");
        }
        // Solicitação recusada antes? Limpa e permite tentar de novo
        if (!existentes.isEmpty()) {
            listaAmigosRepository.deleteAll(existentes);
            listaAmigosRepository.flush();
        }

        ListaAmigos relacao = new ListaAmigos();
        relacao.setId(new ListaAmigosId(usuarioId, amigoId));
        relacao.setUsuario(usuario);
        relacao.setAmigo(amigo);
        relacao.setStatusAmizade(StatusAmizade.PENDENTE);
        relacao.setDataAmizade(LocalDateTime.now());
        listaAmigosRepository.save(relacao);
    }

    @Transactional
    public void responderSolicitacao(Long usuarioId, Long remetenteId, StatusAmizade novoStatus) {
        if (novoStatus != StatusAmizade.ACEITA && novoStatus != StatusAmizade.RECUSADA) {
            throw new IllegalArgumentException("Status inválido: use ACEITA ou RECUSADA.");
        }

        ListaAmigos relacao = listaAmigosRepository.findById(new ListaAmigosId(remetenteId, usuarioId))
                .orElseThrow(() -> new NotFoundException("Solicitação de amizade não encontrada."));

        if (relacao.getStatusAmizade() != StatusAmizade.PENDENTE) {
            throw new IllegalArgumentException("Esta solicitação já foi respondida.");
        }

        relacao.setStatusAmizade(novoStatus);
        listaAmigosRepository.save(relacao);
    }

    public List<AmizadeResponseDTO> listarAmigos(Long usuarioId) {
        return paraDto(listaAmigosRepository.findDoUsuarioPorStatus(usuarioId, StatusAmizade.ACEITA), usuarioId);
    }

    public List<AmizadeResponseDTO> listarSolicitacoesRecebidas(Long usuarioId) {
        return paraDto(listaAmigosRepository.findRecebidasPorStatus(usuarioId, StatusAmizade.PENDENTE), usuarioId);
    }

    public List<AmizadeResponseDTO> listarSolicitacoesEnviadas(Long usuarioId) {
        return paraDto(listaAmigosRepository.findEnviadasPorStatus(usuarioId, StatusAmizade.PENDENTE), usuarioId);
    }

    @Transactional
    public void removerAmizade(Long usuarioId, Long amigoId) {
        List<ListaAmigos> relacoes = listaAmigosRepository.findRelacaoEntre(usuarioId, amigoId);
        if (relacoes.isEmpty()) {
            throw new NotFoundException("Relação de amizade não encontrada.");
        }
        listaAmigosRepository.deleteAll(relacoes);
    }

    private List<AmizadeResponseDTO> paraDto(List<ListaAmigos> relacoes, Long usuarioId) {
        return relacoes.stream().map(r -> new AmizadeResponseDTO(r, usuarioId)).toList();
    }
}
