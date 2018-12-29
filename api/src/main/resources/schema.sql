CREATE TABLE IF NOT EXISTS api
(
  method TEXT NOT NULL,
  path TEXT NOT NULL,
  version BIGINT NOT NULL DEFAULT (extract(EPOCH FROM now()) * (1000)::BIGINT),
  default_query_string TEXT,
  title TEXT,
  description TEXT,
  sql_text TEXT NOT NULL,
  PRIMARY KEY (method, path, version)
);

CREATE TABLE IF NOT EXISTS link
(
  api_method TEXT NOT NULL,
  api_path TEXT NOT NULL,
  api_version_min BIGINT DEFAULT 0,
  api_version_max BIGINT DEFAULT 9223372036854775807,
  method TEXT NOT NULL,
  link TEXT NOT NULL,
  name TEXT,
  type TEXT,
  icon TEXT
);
