package com.bennyboy12306.betterMultiplayerSleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class handles resetting the no-sleep rule
 * @author Bennyboy12306
 */
public class NewDayTask extends BukkitRunnable
{
    private final BetterMultiplayerSleep plugin;

    /**
     * Constructor.
     * @param plugin a reference to the main plugin class.
     */
    public NewDayTask(BetterMultiplayerSleep plugin) {
        this.plugin = plugin;
    }

    /**
     * Run event, reset sleep percent if morning time
     */
    @Override
    public void run()
    {
        // Get the time
        // Get the overworld
        World world = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElse(null);

        // If no overworld is found, return (to avoid null issues)
        if (world == null) return;

        long time = world.getTime();

        // If it is morning (between 6am and 7am) and no-sleep was set (from the previous night) reset it
        if (time >= 0 && time <= 1000 && plugin.getNoSleep()) {
            plugin.setNoSleep(false);
            plugin.setControlPlayerName(null);

            FileConfiguration config = plugin.getConfig();
            String sleepPercent = config.getString("default-sleep-percent");
            CommandSender cmdSender = Bukkit.getConsoleSender();
            String cmd = "gamerule playersSleepingPercentage " + sleepPercent;
            Bukkit.dispatchCommand(cmdSender, cmd);

            // Load in color from config file
            String color = config.getString("message-color");
            color = color.replace("&", "§");

            Bukkit.broadcastMessage(color + "No-Sleep Reset: Players sleeping percent is now: " + sleepPercent + "%");
        }
    }
}
