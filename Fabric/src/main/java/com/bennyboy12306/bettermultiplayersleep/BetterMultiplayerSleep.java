package com.bennyboy12306.bettermultiplayersleep;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Objects;

/**
 * This class is the main entry point for the mod.
 * @author Bennyboy12306
 */

public class BetterMultiplayerSleep implements ModInitializer {
    private int defaultSleepPercent = 0;
    private boolean noSleep = false;
    private String controlPlayerName = null;

    /**
     * This method is called when the mod is initialized.
     */
    @Override
    public void onInitialize() {
        System.out.println("Better Multiplayer Sleep Mod Initialized");

        // Load the config file
        loadConfig();

        // Register commands
        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    /**
     * This method registers the no-sleep command.
     * @param dispatcher the command dispatcher.
     * @param commandRegistryAccess the command registry access.
     * @param registrationEnvironment the registration environment.
     */
    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("no-sleep")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerWorld world = source.getServer().getOverworld();

                    if (!noSleep) {
                        // Prevent Skipping this night by disabling multiplayer sleep
                        noSleep = true;
                        controlPlayerName = Objects.requireNonNull(context.getSource().getPlayer()).getName().getString();
                        setPlayerSleepingPercentage(world, 100);
                        source.getServer().getPlayerManager().broadcast(Text.literal("§1No-Sleep Requested: All players must sleep to skip to day"), false);
                        source.sendFeedback(() -> Text.literal("Use the command again to reset to default sleep percent"), false);
                    } else {
                        // Reset sleep functionality
                        if (Objects.requireNonNull(context.getSource().getPlayer()).getName().getString().equals(controlPlayerName)) { // Only the person who requested this can disable it
                            resetSleepSettings(world, source);
                        } else {
                            source.sendFeedback(() -> Text.literal("No-Sleep already requested, only the person who requested this can disable it"), false);
                        }
                    }
                    return 1;
                }));
    }

    // Todo fix this, does not reset the day as intended
    // Todo sometimes no-sleep does not work, It gets called as the message appears but the sleep percentage does not change, as seen by the fact that only one player needs to sleep to skip the night

    /**
     * This method checks if a new day has started and resets the sleep settings if so.
     * @param server The Minecraft server instance.
     */
    private void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey() == World.OVERWORLD) {
                long time = world.getTimeOfDay();  // Get the current world time

                // If it is morning (between 6am and 7am) and no-sleep was set (from the previous night) reset it
                if (time >= 0 && time <= 1000 && noSleep) {
                    resetSleepSettings(world, server.getCommandSource());
                }
            }
        }
    }

    /**
     * This method resets the sleep percent and variables.
     * @param world The world to reset the settings in.
     */
    private void resetSleepSettings(ServerWorld world , ServerCommandSource source) {
        // Reset the playerSleepingPercentage to default
        setPlayerSleepingPercentage(world, defaultSleepPercent);

        // Reset the no sleep flag and control player name
        noSleep = false;
        controlPlayerName = null;
        source.getServer().getPlayerManager().broadcast(Text.literal("§1No-Sleep Reset: Players sleeping percent is now: " + defaultSleepPercent + "%"), false);
    }

    /**
     * This method sets the playerSleepingPercentage gamerule.
     * @param world the world to set the gamerule in.
     * @param percentage the new value for the playerSleepingPercentage.
     */
    public void setPlayerSleepingPercentage(ServerWorld world, int percentage) {
        percentage = Math.max(0, Math.min(100, percentage));

        // Set the playerSleepingPercentage gamerule
        world.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE).set(percentage, world.getServer());
    }

    /**
     * This method loads default sleep percentage from the config file or creates the default config file if none exists.
     */
    private void loadConfig() {
        // Define the path to the config file
        Path configFilePath = Paths.get("config", "Better-Multiplayer-Sleep", "config.json");

        // Check if the config file exists
        if (Files.exists(configFilePath)) {
            try {
                // Read the existing config file
                String jsonContent = Files.readString(configFilePath);
                Gson gson = new Gson();
                JsonObject config = gson.fromJson(jsonContent, JsonObject.class);

                // Check if the config is valid
                if (config != null && config.has("defaultSleepPercent")) {
                    defaultSleepPercent = config.get("defaultSleepPercent").getAsInt();
                } else {
                    System.out.println("Invalid config.json or missing 'defaultSleepPercent' field.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (com.google.gson.JsonSyntaxException e) {
                System.out.println("Error parsing the config file. It might be malformed.");
                e.printStackTrace();
            }
        } else {
            // If the config file doesn't exist, create it with default data
            System.out.println("Config file not found. Creating default config.");

            // Create a new JsonObject with default data
            JsonObject defaultConfig = new JsonObject();
            defaultConfig.addProperty("defaultSleepPercent", 0);

            // Create the necessary directories and the config file
            try {
                Files.createDirectories(configFilePath.getParent()); // Ensure the parent directories exist
                Files.writeString(configFilePath, defaultConfig.toString()); // Write the default data
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}