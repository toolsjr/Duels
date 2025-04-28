package com.tools.duels;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Сохраняем конфиг по умолчанию и загружаем
        saveDefaultConfig();
        config = getConfig();

        // Инициализация менеджеров с передачей конфига
        this.arenaManager = new ArenaManager(this, config);
        this.scoreboardManager = new ScoreboardManager(this);
        this.gameManager = new GameManager(this, arenaManager, scoreboardManager, config);

        // Регистрация команд и событий
        getCommand("duels").setExecutor(new CommandManager(this, gameManager));
        getServer().getPluginManager().registerEvents(new EventListener(this, gameManager, scoreboardManager), this);

        // Create arena
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
}