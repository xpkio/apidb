CREATE TABLE IF NOT EXISTS api
(
  id SERIAL NOT NULL CONSTRAINT api_sql_pkey PRIMARY KEY,
  method TEXT NOT NULL,
  path TEXT NOT NULL,
  title TEXT,
  description TEXT,
  sql_text TEXT NOT NULL,
  version BIGINT NOT NULL DEFAULT (extract(EPOCH FROM now()) * (1000)::BIGINT)
);