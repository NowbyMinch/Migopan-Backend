public record AtualizarMembroRequestDTO(
    @Pattern(regexp = "ADMIN|MEMBRO", mensagem = "O papel deve ser 'ADMIN' ou 'MEMBRO'")
    String papel,
    
    Boolean bloqueado
){}