# Configuration values for NovaMain
Optional values are not required but might be needed for special server configurations

---
## Database options
* `use_global_settings` [bool] (Optional) This options enables reading database config from novaconfig.json in the home directory. This is enabled by default and has to be set to false to disable
* `mysql.driver` [string] The MySQL driver to use
* `mysql.host` [string] The host of the database server
* `mysql.username` [string] The username for the database
* `mysql.password` [string] The password to use for the database
* `mysql.database` [string] The MySQL database to use
---
## Server options
* `server_type` [string] The server type this server is running
* `name_override` [string] (Optional) Override the name generation and use the providen name instead
* `host` [string] The host that should be used to access this server thru bungeecord
* `global_lobby_fallback` [string] The lobby to use if the server type does not define a lobby or if the server fail to send the player to that server

---
## Game options
* `team_manager` [string] (Optional) The team manager to use. See Team Managers for avaliable options
* `no_pearl_damage` [bool] (Optional) set to true to disable pearl damage
* `compass_tracker_mode` [string] (Optional) set what compass tracker mode to use. If this is not set the server wont use compass trackers. See Compass Trackers for avaliable options
* `compass_tracker_strict_mode` [bool] (Optional) Set to false to disable strict mode
* `game_starter` [string] (Optional) Set the game starter to use. See Game Starters for avaliable options
* `use_teams` [bool] (Optional) Set to true to use teams
* `time_limit` [int] (Optional) Time limit in seconds for games
* `time_limit_countdown_line` [int] (Optional) The scoreboard line for time limit countdowns
---
## Additional options
* `skywars_solo_team_count` [int] (Optional) Set the amount of solo teams for the skywars_solo team manager
* `solo_team_count` [int] (Optional) Set the amount of solo teams for the solo team manager
* `disable_scoreboard` [bool] (Optional) Disable the scoreboard
* `set_reconnect_server` [bool] (Optional) If enabled the player can use /reconnect to reconnect to this server

* `enable_golden_heads` [bool] enable the golden heads module
* `enable_edible_heads` [bool] enable the edible heads module
* `enable_player_head_drop` [bool] enable the player head drop module
---

# Team Managers
Here are the available team managers
* `skywars_solo` Solo teams used for skywars
* `solo` Solo teams for other games

# Compass Trackers
Here are the available compass trackers
* `closest_player` Track closest player ignoring teams

# Game Starters
Here are the available game starters
* `DefaultCountdownGameStarter` Starts the countdown when a certain amount of players has joined