package com.bennyboy12306.betterMultiplayerSleep;

import commands.NoSleepCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is the main entry point for the plugin.
 * @author Bennyboy12306
 */

public final class BetterMultiplayerSleep extends JavaPlugin
{

    private boolean noSleep = false;
    private String controlPlayerName;

    /**
     * This method handles enabling the plugin.
     */
    @Override
    public void onEnable()
    {
        // Plugin startup logic

        //Initialise bStats
        int pluginId = 23625; //If you have modified this plugin you may want to change this ID to your own
        Metrics metrics = new Metrics(this, pluginId);

        // Save default config if no config exists
        saveDefaultConfig();

        // Register the commands
        getCommand("no-sleep").setExecutor(new NoSleepCommand(this));

        // Get the time then initialize and start the day time task when the plugin is enabled
        // Get the overworld
        World world = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElse(null);

        // If no overworld is found, return (to avoid null issues)
        if (world == null) return;

        long time = world.getTime();

        /**
         * @Todo Fix this. Reset does not happen automatically
         */
        new NewDayTask(this, time).runTaskTimer(this, 0L, 200L); // Check every 200 ticks (10 seconds)

        //Log plugin start up
        getLogger().info("The Better Multiplayer Sleep Plugin has been enabled");

    }

    /**
     * This method handles disabling the plugin.
     */
    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

        //Reset playerSleepingPercentage
        String sleepPercent = getConfig().getString("default-sleep-percent");
        CommandSender cmdSender = Bukkit.getConsoleSender();
        String cmd = "gamerule playersSleepingPercentage " + sleepPercent;
        Bukkit.dispatchCommand(cmdSender, cmd);

        //Log plugin shut down
        getLogger().info("The Better Multiplayer Sleep Plugin has been disabled");
    }

    /**
     * This method gets the current value of noSleep
     * @return if noSleep rule is active
     */
    public boolean getNoSleep()
    {
        return noSleep;
    }

    /**
     * This method sets the value of noSleep
     * @param noSleeping the new value for noSleep
     */
    public void setNoSleep(boolean noSleeping)
    {
        noSleep = noSleeping;
    }

    /**
     * This method gets the current value of controlPlayerName
     * @return the name of the control player
     */
    public String getControlPlayerName() {
        return controlPlayerName;
    }

    /**
     * This method sets the value of controlPlayerName
     * @param playerName the new value for controlPlayerName
     */
    public void setControlPlayerName(String playerName) {
        controlPlayerName = playerName;
    }
}
