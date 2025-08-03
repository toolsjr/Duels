package com.tools.duels;

import org.bukkit.block.data.type.Comparator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private ModeManager modeManager;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        this.arenaManager = new ArenaManager(this, config);
        this.scoreboardManager = new ScoreboardManager(this);
        this.gameManager = new GameManager(this, arenaManager, scoreboardManager, config);
        this.modeManager = new ModeManager(this);

        getCommand("duels").setExecutor(new CommandManager(this, gameManager));
        getServer().getPluginManager().registerEvents(new EventListener(this, gameManager, modeManager), this);

        arenaManager.createArena();

        getLogger().info("Duels plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Duels plugin has been disabled!");
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ModeManager getModeManager() {
        return modeManager;
    }
}