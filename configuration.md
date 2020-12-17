# Configuration values for NovaMain
Optional values are not required but might be needed for special server configurations

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
* `game_starter` (Optional) Set the game starter to use. See Game Starters for avaliable options
* `use_teams` (Optional) Set to true to use teams
---
## Additional options
* `skywars_solo_team_count` (Optional) Set the amount of solo teams for the skywars_solo team manager
* `disable_scoreboard` (Optional) Disable the scoreboard

---

# Team Managers
Here are the available team managers
* `skywars_solo` Solo teams used for skywars

# Compass Trackers
Here are the available compass trackers
* `closest_player` Track closest player ignoring teams

# Game Starters
Here are the available game starters
* `DefaultCountdownGameStarter` Starts the countdown when a certain amount of players has joined