INSERT INTO api (method, path, title, description, sql_text)
SELECT 'GET', '/', 'APIs', 'Shows available APIs', 'SELECT * FROM api'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'GET' AND path = '/');

INSERT INTO api (method, path, title, description, sql_text)
SELECT 'POST', '/', 'New API', 'Create a new API', 'INSERT INTO api (method, path, title, description, sql_text)
VALUES (:method, :path, :title, :description, :sql_text)'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'POST' AND path = '/');
