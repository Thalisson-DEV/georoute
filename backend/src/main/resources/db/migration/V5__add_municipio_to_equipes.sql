ALTER TABLE equipes ADD COLUMN municipio VARCHAR(255);

UPDATE equipes SET municipio = 'JUAZEIRO' WHERE municipio IS NULL;

ALTER TABLE equipes ALTER COLUMN municipio SET NOT NULL;
