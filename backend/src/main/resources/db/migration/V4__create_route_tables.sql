CREATE TABLE equipes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    latitude_base DOUBLE PRECISION,
    longitude_base DOUBLE PRECISION,
    setor VARCHAR(50)
);

CREATE TABLE execucoes_rota (
    id VARCHAR(255) PRIMARY KEY,
    equipe_id BIGINT,
    data DATE,
    rota_json TEXT,
    CONSTRAINT fk_equipe FOREIGN KEY (equipe_id) REFERENCES equipes(id)
);