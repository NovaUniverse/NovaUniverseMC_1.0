CREATE PROCEDURE `store_chat_message`(
	IN uuid VARCHAR(36),
    IN is_command TINYINT(1),
    IN is_canceled TINYINT(1),
    IN content TEXT
)
BEGIN
	DECLARE player_id INT UNSIGNED;
    SET player_id = 0;
    
    SELECT pl.id INTO player_id FROM players AS pl
		WHERE pl.uuid = uuid
        LIMIT 1;
        
	IF player_id > 0
    THEN
		INSERT INTO chat_log
			(player_id, content, is_command, canceled)
            VALUES
            (player_id, content, is_command, is_canceled);
    END IF;
END