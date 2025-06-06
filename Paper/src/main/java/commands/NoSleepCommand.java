package commands;

import com.bennyboy12306.betterMultiplayerSleep.BetterMultiplayerSleep;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * This class allows players to toggle no multiplayer sleep
 * @author Bennyboy12306
 */

public class NoSleepCommand implements CommandExecutor
{
    private final BetterMultiplayerSleep plugin;

    public NoSleepCommand(BetterMultiplayerSleep plugin)
    {
        this.plugin = plugin;
    }

    /**
     * This method handles the no-sleep command.
     * @param sender the thing that sent the command.
     * @param command the command that was sent.
     * @param label the command label.
     * @param args the arguments that were passed for this command.
     * @return if the command was successfully handled (returning false automatically sends the usage message, so do not return false if you already provide an error message with usage in it).
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (sender.hasPermission("better-multiplayer-sleep.base"))
        {
            // Load in color from config file
            FileConfiguration config = plugin.getConfig();
            String color = config.getString("message-color");
            color = color.replace("&", "§");
            if (!plugin.getNoSleep())
            {
                //Prevent Skipping this night by disabling multiplayer sleep
                plugin.setNoSleep(true);
                plugin.setControlPlayerName(sender.getName());

                CommandSender cmdSender = Bukkit.getConsoleSender();
                String cmd = "gamerule playersSleepingPercentage 100";
                Bukkit.dispatchCommand(cmdSender, cmd);

                Bukkit.broadcastMessage(color + "No-Sleep Requested: All players must sleep to skip to day");
                sender.sendMessage("Use the command again to reset to default sleep percent");
            }
            else //Reset sleep functionality
            {
                //If player requested no sleep they are the only one who can reset it
                if (sender.getName().equals(plugin.getControlPlayerName()))
                {
                    String sleepPercent = config.getString("default-sleep-percent");
                    CommandSender cmdSender = Bukkit.getConsoleSender();
                    String cmd = "gamerule playersSleepingPercentage " + sleepPercent;
                    Bukkit.dispatchCommand(cmdSender, cmd);

                    Bukkit.broadcastMessage(color + "No-Sleep Reset: Players sleeping percent is now: " + sleepPercent + "%");
                    plugin.setNoSleep(false);
                    plugin.setControlPlayerName(null);
                }
                else
                {
                    sender.sendMessage("No-Sleep already requested, only the person who requested this can disable it");
                }
            }
            return true;
        }
        else
        {
            sender.sendMessage( "You don't have permission to execute this command.");
        }
        return true;
    }
}
