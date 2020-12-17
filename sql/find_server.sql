CREATE PROCEDURE `find_server`(
	IN serverTypeId INT
)
BEGIN
	SELECT s.id, s.name
	FROM servers AS s
		LEFT JOIN players AS p
		ON p.server_id = s.id AND p.is_online = 1
		JOIN server_type AS t
		ON t.id = s.type_id
	WHERE
		s.type_id = serverTypeId
        AND s.has_failed = 0
        AND s.minigame_started = 0
        AND TIMESTAMPDIFF(MINUTE, s.heartbeat, CURRENT_TIMESTAMP) < 1
	GROUP BY
		s.id,
		s.name,
        s.has_failed,
        s.minigame_started,
		t.soft_player_limit,
		t.target_player_count,
        s.heartbeat
	HAVING COUNT(p.id) < t.soft_player_limit
	ORDER BY
	CASE WHEN (COUNT(p.id) < t.target_player_count)
		THEN 0
		ELSE COUNT(p.id)
	END ASC
	LIMIT 1;
END