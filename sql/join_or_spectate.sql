CREATE PROCEDURE `join_or_spectate` (
	IN in_username VARCHAR(16)
)
BEGIN
	SELECT
		p.is_online AS is_online,
		p.username AS username,
		s.name AS server_name,
		s.minigame_started AS game_started,
		t.allow_join_command AS allow_join,
		t.allow_spectate_command AS allow_spectate
	FROM players AS p
	LEFT JOIN servers AS s
		ON s.id = p.server_id
	LEFT JOIN server_type AS t
		ON t.id = s.type_id
	WHERE LOWER(p.username) = LOWER(in_username) LIMIT 1;
END