CREATE PROCEDURE `get_extended_stats` ()
BEGIN
	SELECT
		st.id AS server_type_id,
		st.name AS server_type_name,
		st.display_name AS server_type_display_name,
		count(p.id) AS player_count,
		count(distinct(s.id)) AS total_server_count
	FROM server_type AS st
		LEFT JOIN servers AS s ON s.type_id = st.id
		LEFT JOIN players AS p ON p.server_id = s.id AND p.is_online = 1
	WHERE st.show_in_web_api = 1
	GROUP BY st.id;
END