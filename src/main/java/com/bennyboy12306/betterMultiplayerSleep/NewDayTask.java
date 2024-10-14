package com.bennyboy12306.betterMultiplayerSleep;

import org.bukkit.Bukkit;
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
    private long time;

    /**
     * Constructor.
     * @param plugin a reference to the main plugin class.
     */
    public NewDayTask(BetterMultiplayerSleep plugin, long time) {
        this.plugin = plugin;
        this.time = time;
    }

    /**
     * Run event, reset sleep percent if morning time
     */
    @Override
    public void run()
    {
        // If it is morning and no-sleep was set (from the previous night) reset it
        if (time >= 0 && time <= 1000 && plugin.getNoSleep()) {
            plugin.setNoSleep(false);
            plugin.setControlPlayerName(null);

            FileConfiguration config = plugin.getConfig();
            String sleepPercent = config.getString("default-sleep-percent");
            CommandSender cmdSender = Bukkit.getConsoleSender();
            String cmd = "gamerule playersSleepingPercentage " + sleepPercent;
            Bukkit.dispatchCommand(cmdSender, cmd);

            Bukkit.broadcastMessage("§1No-Sleep Reset: Players sleeping percent is now: " + sleepPercent + "%");
        }
    }
}
