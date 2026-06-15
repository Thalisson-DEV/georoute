-- Índices aditivos e idempotentes. Não recriam tabelas (schema já existe via Flyway).
-- Otimizam as queries de histórico/limpeza de rotas e os filtros de equipes.

-- Histórico por equipe ordenado por data (findAllByEquipeIdOrderByDataDesc)
-- e busca por (equipe_id, data) no upsert diário de execução.
CREATE INDEX IF NOT EXISTS idx_execucoes_rota_equipe_data ON execucoes_rota (equipe_id, data DESC);

-- Limpeza diária (deleteOlderThan: WHERE data < cutoff).
CREATE INDEX IF NOT EXISTS idx_execucoes_rota_data ON execucoes_rota (data);

-- Filtros de equipes: (setor, municipio) cobre também filtro só por setor (coluna líder).
CREATE INDEX IF NOT EXISTS idx_equipes_setor_municipio ON equipes (setor, municipio);

-- Filtro só por município.
CREATE INDEX IF NOT EXISTS idx_equipes_municipio ON equipes (municipio);

-- Verificação de nome duplicado (existsByNome) em criação/atualização de equipe.
CREATE INDEX IF NOT EXISTS idx_equipes_nome ON equipes (nome);
