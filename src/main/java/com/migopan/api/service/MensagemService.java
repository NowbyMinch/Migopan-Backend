package com.migopan.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.MensagemDTOs.EnviarMensagemRequestDTO;
import com.migopan.api.dto.MensagemDTOs.MensagemResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Mensagem;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.MensagemRepository;
import com.migopan.api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MensagemService {

    private static final int TAMANHO_MAXIMO_TEXTO = 2000;
    private static final int LIMITE_MAXIMO_PAGINA = 100;

    private final MensagemRepository mensagemRepository;
    private final GrupoRepository grupoRepository;
    private final GrupoMembroRepository grupoMembroRepository;
    private final UsuarioRepository usuarioRepository;

    public List<MensagemResponseDTO> listar(Long grupoId, Long usuarioId, Long antesDe, int limite) {
        exigirMembro(grupoId, usuarioId);

        Pageable pagina = PageRequest.of(0, Math.min(Math.max(limite, 1), LIMITE_MAXIMO_PAGINA));
        List<Mensagem> encontradas = antesDe == null
                ? mensagemRepository.findByGrupoIdOrderByIdDesc(grupoId, pagina)
                : mensagemRepository.findByGrupoIdAndIdLessThanOrderByIdDesc(grupoId, antesDe, pagina);

        List<MensagemResponseDTO> resposta = new ArrayList<>(encontradas.stream().map(MensagemResponseDTO::new).toList());
        Collections.reverse(resposta);
        return resposta;
    }

    @Transactional
    public MensagemResponseDTO enviar(Long grupoId, Long usuarioId, EnviarMensagemRequestDTO dto) {
        String texto = dto == null || dto.texto() == null ? "" : dto.texto().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("A mensagem não pode ser vazia.");
        }
        if (texto.length() > TAMANHO_MAXIMO_TEXTO) {
            throw new IllegalArgumentException("A mensagem deve ter no máximo " + TAMANHO_MAXIMO_TEXTO + " caracteres.");
        }

        exigirMembro(grupoId, usuarioId);

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        Mensagem mensagem = new Mensagem();
        mensagem.setGrupo(grupoRepository.getReferenceById(grupoId));
        mensagem.setUsuario(autor);
        mensagem.setTexto(texto);

        return new MensagemResponseDTO(mensagemRepository.save(mensagem));
    }

    private void exigirMembro(Long grupoId, Long usuarioId) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(grupoId, usuarioId)) {
            throw new AcessoNegadoException("Você não faz parte deste grupo.");
        }
    }
}
