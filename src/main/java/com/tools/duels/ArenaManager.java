package com.tools.duels;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaManager {
    private final JavaPlugin plugin;
    private Location arenaSpawn;
    private Location player1Spawn;
    private Location player2Spawn;

    public ArenaManager(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        loadArenaLocations(config);
    }

    private void loadArenaLocations(FileConfiguration config) {
        World world = Bukkit.getWorld(config.getString("arena.spawn.world"));

        this.arenaSpawn = new Location(
                world,
                config.getDouble("arena.spawn.x"),
                config.getDouble("arena.spawn.y"),
                config.getDouble("arena.spawn.z")
        );

        this.player1Spawn = new Location(
                world,
                config.getDouble("arena.player1.x"),
                config.getDouble("arena.player1.y"),
                config.getDouble("arena.player1.z")
        );

        this.player2Spawn = new Location(
                world,
                config.getDouble("arena.player2.x"),
                config.getDouble("arena.player2.y"),
                config.getDouble("arena.player2.z")
        );
    }

    public void createArena() {
        World world = Bukkit.getWorlds().get(0);
        // Set spawn location at x=0, y=100, z=0 (adjust as needed)
        this.arenaSpawn = new Location(world, 0, 100, 0);

        // You can add arena setup logic here (building platforms, etc.)
    }

    public Location getArenaSpawn() {
        return arenaSpawn;

    }

    public Location getPlayer1Spawn() {
        return player1Spawn;
    }

    public Location getPlayer2Spawn() {
        return player2Spawn;
    }
}