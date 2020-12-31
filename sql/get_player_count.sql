CREATE PROCEDURE `get_player_count`()
BEGIN
	SELECT st.id AS server_type_id, COUNT(pl.id) AS player_count FROM server_type AS st
		JOIN servers AS srv
		ON srv.type_id = st.id
		LEFT JOIN players AS pl
		ON pl.server_id = srv.id AND pl.is_online = 1
		GROUP BY st.id;
END