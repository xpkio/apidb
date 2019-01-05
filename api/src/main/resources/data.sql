INSERT INTO api VALUES (1, 'GET', '/apis', DEFAULT, DEFAULT, 'APIs', 'Shows APIs');
INSERT INTO api VALUES (2, 'GET', '/sqlScripts', DEFAULT, DEFAULT, 'SQL Scripts', 'Shows SQL Scripts');
INSERT INTO api VALUES (3, 'GET', '/links', DEFAULT, DEFAULT, 'Links', 'Shows Links');
INSERT INTO api VALUES (4, 'POST', '/apis', DEFAULT, DEFAULT, 'New API', 'Create a new API');
INSERT INTO api VALUES (5, 'POST', '/sqlScripts', DEFAULT, DEFAULT, 'New SQL Script', 'Create a new SQL Script');
INSERT INTO api VALUES (6, 'POST', '/links', DEFAULT, DEFAULT, 'New Link', 'Create a new Link');
INSERT INTO api VALUES (7, 'GET', '/sqlScriptsForApi', DEFAULT, DEFAULT, 'SQL for Api', 'Shows SQL scripts attached to a specified API');
INSERT INTO api VALUES (8, 'GET', '/linksForApi', DEFAULT, DEFAULT, 'Links for Api', 'Shows links attached to a specified API');
INSERT INTO api VALUES (9, 'POST', '/sqlScriptsForApi', DEFAULT, 'resultKeyName=result', 'Add SQL for Api', 'Adds SQL scripts to a specified API');
INSERT INTO api VALUES (10, 'POST', '/linksForApi', DEFAULT, DEFAULT, 'Add Links for Api', 'Add links to a specified API');

INSERT INTO sql_script VALUES (1, DEFAULT, DEFAULT, DEFAULT, 'SELECT * FROM api');
INSERT INTO sql_script VALUES (2, DEFAULT, DEFAULT, DEFAULT, 'SELECT * FROM sql_script');
INSERT INTO sql_script VALUES (3, DEFAULT, DEFAULT, DEFAULT, 'SELECT * FROM link');
INSERT INTO sql_script VALUES (4, DEFAULT, DEFAULT, DEFAULT, 'INSERT INTO api VALUES (DEFAULT, :apiMethod, :apiPath, DEFAULT, DEFAULT, :apiTitle, :apiDescription)');
INSERT INTO sql_script VALUES (5, DEFAULT, DEFAULT, DEFAULT, 'INSERT INTO sql_script VALUES (DEFAULT, :sqlScriptName, :sqlScriptDescription, :executeOnApiDbInsteadOfTenantDb, :sqlText)');
INSERT INTO sql_script VALUES (6, DEFAULT, DEFAULT, DEFAULT, 'INSERT INTO link VALUES (DEFAULT, :apiMethod, :apiPath, :apiVersionMin, :apiVersionMax, :linkMethod, :link, :linkName, :linkType, :linkIcon)');
INSERT INTO sql_script VALUES (7, DEFAULT, DEFAULT, DEFAULT, 'SELECT sql_script.* FROM sql_script
  JOIN api_to_sql_script atss ON sql_script.sql_script_id = atss.sql_script_id
  JOIN api a ON atss.api_id = a.api_id
WHERE a.api_id = :apiId;');
INSERT INTO sql_script VALUES (8, DEFAULT, DEFAULT, DEFAULT, 'SELECT link.* FROM link
  JOIN api_to_link atl ON link.link_id = atl.link_id
  JOIN api a ON atl.api_id = a.api_id
WHERE a.api_id = :apiId;');
INSERT INTO sql_script VALUES (9, DEFAULT, DEFAULT, DEFAULT, 'INSERT INTO api_to_sql_script VALUES (:apiId, :sqlScriptId, :resultKeyName)');
INSERT INTO sql_script VALUES (10, DEFAULT, DEFAULT, DEFAULT, 'INSERT INTO api_to_link VALUES (:apiId, :linkId)');

INSERT INTO api_to_sql_script VALUES (1, 1, DEFAULT);
INSERT INTO api_to_sql_script VALUES (2, 2, DEFAULT);
INSERT INTO api_to_sql_script VALUES (3, 3, DEFAULT);
INSERT INTO api_to_sql_script VALUES (4, 4, DEFAULT);
INSERT INTO api_to_sql_script VALUES (5, 5, DEFAULT);
INSERT INTO api_to_sql_script VALUES (6, 6, DEFAULT);
INSERT INTO api_to_sql_script VALUES (7, 7, DEFAULT);
INSERT INTO api_to_sql_script VALUES (8, 8, DEFAULT);
INSERT INTO api_to_sql_script VALUES (9, 9, DEFAULT);
INSERT INTO api_to_sql_script VALUES (10, 10, DEFAULT);

INSERT INTO link VALUES (1, 'POST', '/apis', 'New Api', 'headline', 'plus');
INSERT INTO link VALUES (2, 'POST', '/sqlScripts', 'New SQL Script', 'headline', 'plus');
INSERT INTO link VALUES (3, 'POST', '/links', 'New Link', 'headline', 'plus');
INSERT INTO link VALUES (4, 'GET', '/sqlScriptsForApi?apiId=$apiId', 'SQL Scripts', 'inline', 'document');
INSERT INTO link VALUES (5, 'GET', '/linksForApi?apiId=$apiId', 'Links', 'inline', 'link');
INSERT INTO link VALUES (6, 'POST', '/sqlScriptsForApi?apiId=$apiId&linkId=$?linkId', 'Add SQL Script', 'headline', 'plus');
INSERT INTO link VALUES (7, 'POST', '/linksForApi?apiId=$apiId&linkId=$?linkId', 'Add Link', 'headline', 'plus');

INSERT INTO api_to_link VALUES (1, 1);
INSERT INTO api_to_link VALUES (2, 2);
INSERT INTO api_to_link VALUES (3, 3);
INSERT INTO api_to_link VALUES (1, 4);
INSERT INTO api_to_link VALUES (1, 5);
INSERT INTO api_to_link VALUES (7, 6);
INSERT INTO api_to_link VALUES (8, 7);

ALTER SEQUENCE api_api_id_seq RESTART WITH 101;
ALTER SEQUENCE link_link_id_seq RESTART WITH 101;
ALTER SEQUENCE sql_script_sql_script_id_seq RESTART WITH 101;

