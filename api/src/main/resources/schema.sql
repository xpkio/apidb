DROP TABLE IF EXISTS api CASCADE;
DROP TABLE IF EXISTS sql_script CASCADE;
DROP TABLE IF EXISTS link CASCADE;
DROP TABLE IF EXISTS api_to_sql_script CASCADE;
DROP TABLE IF EXISTS api_to_link CASCADE;

CREATE TABLE api
(
  api_id                   SERIAL PRIMARY KEY,
  api_method               TEXT   NOT NULL,
  api_path                 TEXT   NOT NULL,
  api_version              BIGINT NOT NULL DEFAULT (extract(EPOCH FROM now()) * (1000)::BIGINT),
  api_default_query_string TEXT   NOT NULL DEFAULT '',
  api_title                TEXT   NOT NULL DEFAULT '',
  api_description          TEXT   NOT NULL DEFAULT '',
  UNIQUE (api_method, api_path, api_version)
);

CREATE TABLE sql_script
(
  sql_script_id                          SERIAL PRIMARY KEY,
  sql_script_name                        TEXT    NOT NULL DEFAULT '',
  sql_script_description                 TEXT    NOT NULL DEFAULT '',
  execute_on_api_db_instead_of_tenant_db BOOLEAN NOT NULL DEFAULT FALSE,
  sql_text                               TEXT    NOT NULL
);

CREATE TABLE api_to_sql_script
(
  api_id          INT  NOT NULL REFERENCES api,
  sql_script_id   INT  NOT NULL REFERENCES sql_script,
  result_key_name TEXT NOT NULL DEFAULT 'result',
  PRIMARY KEY (api_id, sql_script_id)
);

CREATE TABLE link
(
  link_id     SERIAL PRIMARY KEY,
  link_method TEXT NOT NULL,
  link        TEXT NOT NULL,
  link_name   TEXT NOT NULL,
  link_type   TEXT NOT NULL DEFAULT 'inline',
  link_icon   TEXT NOT NULL DEFAULT ''
);

CREATE TABLE api_to_link
(
  api_id    INT  NOT NULL REFERENCES api,
  link_id   INT  NOT NULL REFERENCES link,
  PRIMARY KEY (api_id, link_id)
);
