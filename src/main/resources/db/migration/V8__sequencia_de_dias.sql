ALTER TABLE tarefa ADD COLUMN concluida_por_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL;
ALTER TABLE tarefa ADD COLUMN data_conclusao DATE;

UPDATE tarefa SET data_conclusao = data_resolucao::date
 WHERE concluida = true AND data_resolucao IS NOT NULL;
UPDATE tarefa SET concluida_por_id = usuario_atribuido_id
 WHERE concluida = true AND usuario_atribuido_id IS NOT NULL;

ALTER TABLE usuario ADD COLUMN ultima_atividade DATE;
ALTER TABLE grupo   ADD COLUMN ultima_atividade DATE;

CREATE INDEX idx_tarefa_conclusao_usuario ON tarefa (concluida_por_id, data_conclusao);
CREATE INDEX idx_tarefa_conclusao_grupo   ON tarefa (grupo_id, data_conclusao);
