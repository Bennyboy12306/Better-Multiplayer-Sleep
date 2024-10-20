# Better Multiplayer Sleep Plugin
This is a simple multiplayer sleep plugin for minecraft paper servers.
Specify a percentage of players who need to sleep to skip the night and allow your players to toggle no-sleeping for a night.

## Usage:
- Simply drag and drop the jar into your plugins folder.
- Modify config.yml to customise the default sleep percentage.
- LIMITATIONS: If you call no sleep between 6am-7am in game, it will be reset automatically, you must call the command after this.
- WARNING: This may conflict with other multiplayer sleep datapacks or plugins, This plugin should handle everything for you so you should be able to safely disable them.

## Commands:
- No Sleep - Toggles No Sleeping for the upcoming night (If no sleeping is already on, only the player who requested it can turn it back off, It will auto reset in the morning):
```
/no-sleep
```
