# Configuration values for NovaMain
---
## Database options
* `mysql.driver` The MySQL driver to use
* `mysql.host` The host of the database server
* `mysql.username` The username for the database
* `mysql.password` The password to use for the database
* `mysql.database` The MySQL database to use
---
## Server options
* `server_type` The server type this server is running
* `name_override` (Optional) Override the name generation and use the providen name instead
* `host` The host that should be used to access this server thru bungeecord
* `global_lobby_fallback` The lobby to use if the seerver type does not define a lobby or if the server fail to send the player to that server

---
## Game options
* `team_manager` (Optional) The team manager to use. See Team Managers for avaliable options
* `no_pearl_damage` (Optional) set to true to disable pearl damage
* `compass_tracker_mode` (Optional) set what compass tracker mode to use. If this is not set the server wont use compass trackers. See Compass Trackers for avaliable options
* `compass_tracker_strict_mode` (Optional) Set to false to disable strict mode


---
# Team Managers
Here are the available team managers
* `skywars_solo`

# Compass Trackers
Here are the available compass trackers
* `closest_player`