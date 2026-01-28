CREATE TABLE clientes (
    instalacao BIGINT PRIMARY KEY,
    conta_contrato BIGINT,
    numero_serie BIGINT,
    numero_poste VARCHAR(100),
    nome_cliente VARCHAR(200),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);