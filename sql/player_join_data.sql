CREATE PROCEDURE `player_join_data`(
	IN uuid VARCHAR(36),
    IN username VARCHAR(16),
    IN ip TEXT
)
BEGIN
	IF NOT EXISTS (
		SELECT p.id FROM players AS p
			WHERE p.uuid = uuid
	)
    THEN
		INSERT INTO players (uuid, username, first_join_timestamp, first_ip_address) VALUES (uuid, username, CURRENT_TIMESTAMP, ip);
    END IF;
    
    UPDATE players SET
		last_join_timestamp = CURRENT_TIMESTAMP,
        last_ip_address = ip,
        players.username = username
	WHERE players.uuid = uuid;
END