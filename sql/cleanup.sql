CREATE PROCEDURE `cleanup`()
BEGIN
	DELETE FROM servers WHERE TIMESTAMPDIFF(MINUTE, servers.heartbeat, CURRENT_TIMESTAMP) >= 3;
    
    UPDATE players SET is_online = 0, server_id = null WHERE TIMESTAMPDIFF(MINUTE, heartbeat_timestamp, CURRENT_TIMESTAMP) >= 3 AND (is_online = 1 OR server_id IS NOT null);
END