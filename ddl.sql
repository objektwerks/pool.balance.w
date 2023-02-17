DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

CREATE TABLE account (
  id BIGSERIAL PRIMARY KEY,
  license VARCHAR(36) UNIQUE NOT NULL,
  email_address VARCHAR UNIQUE NOT NULL,
  pin VARCHAR(7) NOT NULL,
  activated BIGINT NOT NULL,
  deactivated BIGINT NOT NULL
);

CREATE TABLE pool (
  id BIGSERIAL PRIMARY KEY,
  license VARCHAR(36) REFERENCES account(license),
  name VARCHAR(24) NOT NULL,
  volume INT NOT NULL,
  unit VARCHAR NOT NULL
);

CREATE TABLE cleaning (
  id BIGSERIAL PRIMARY KEY,
  pool_id BIGINT REFERENCES pool(id),
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  skimmer_basket BOOL NOT NULL,
  pump_basket BOOL NOT NULL,
  pump_filter BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  cleaned BIGINT NOT NULL
);

CREATE TABLE measurement (
  id BIGSERIAL PRIMARY KEY,
  pool_id BIGINT REFERENCES pool(id),
  total_chlorine INT NOT NULL,
  free_chlorine INT NOT NULL,
  combined_chlorine NUMERIC(2, 1) NOT NULL,
  ph NUMERIC(2, 1) NOT NULL,
  calcium_hardness INT NOT NULL,
  total_alkalinity INT NOT NULL,
  cyanuric_acid INT NOT NULL,
  total_bromine INT NOT NULL,
  salt INT NOT NULL,
  temperature INT NOT NUll,
  measured BIGINT NOT NULL
);

CREATE TABLE chemical (
  id BIGSERIAL PRIMARY KEY,
  pool_id BIGINT REFERENCES pool(id),
  typeof VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL,
  added BIGINT NOT NULL
);

CREATE TABLE fault (
  cause VARCHAR NOT NULL,
  occurred BIGINT NOT NULL
);