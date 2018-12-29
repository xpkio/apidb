INSERT INTO api (method, path, title, description, sql_text)
SELECT 'GET', '/apis', 'APIs', 'Shows APIs', 'SELECT * FROM api'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'GET' AND path = '/apis');

INSERT INTO api (method, path, title, description, sql_text)
SELECT 'POST', '/newApi', 'New API', 'Create a new API', 'INSERT INTO api (method, path, title, description, sql_text)
VALUES (:method, :path, :title, :description, :sql_text)'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'POST' AND path = '/newApi');

INSERT INTO api (method, path, title, description, sql_text)
SELECT 'GET', '/links', 'Links', 'Shows Links', 'SELECT * FROM link'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'GET' AND path = '/links');

INSERT INTO api (method, path, title, description, sql_text)
SELECT 'POST', '/newLink', 'New Link', 'Create a new Link', 'INSERT INTO link (api_method, api_path, api_version, method, link, name, type, icon)
VALUES (:api_method, :api_path, :api_version, :method, :link, :name, :type, :icon)'
WHERE NOT EXISTS (SELECT * FROM api WHERE method = 'POST' AND path = '/newLink');

INSERT INTO link (api_method, api_path, method, link, name, type, icon)
SELECT 'GET', '/apis', '$method', '$path', 'Execute', 'inline', 'play'
WHERE NOT EXISTS (SELECT * FROM link WHERE method = '$method' AND link = '$path' AND name = 'Execute');

INSERT INTO link (api_method, api_path, method, link, name, type, icon)
SELECT 'GET', '/apis', 'POST', '/newApi?method=$prompt&path=$prompt&title=$prompt&description=$prompt&sqlText=$prompt', 'New Api', 'headline', 'plus'
WHERE NOT EXISTS (SELECT * FROM link WHERE method = 'POST' AND link = '/newApi?method=$prompt&path=$prompt&title=$prompt&description=$prompt&sqlText=$prompt' AND name = 'New Api');

INSERT INTO link (api_method, api_path, method, link, name, type, icon)
SELECT 'GET', '/links', 'POST', '/newLink?api_method=$prompt&api_path=$prompt&api_version=$prompt&method=$prompt&link=$prompt&name=$prompt&type=$prompt&icon=$prompt', 'New Link', 'headline', 'plus'
WHERE NOT EXISTS (SELECT * FROM link WHERE method = 'POST' AND link = '/newLink?api_method=$prompt&api_path=$prompt&api_version=$prompt&method=$prompt&link=$prompt&name=$prompt&type=$prompt&icon=$prompt' AND name = 'New Link');
