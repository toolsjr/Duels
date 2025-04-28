package com.tools.duels;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final Main plugin;
    private final GameManager gameManager;

    public EventListener(Main plugin, GameManager gameManager, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        gameManager.addPlayer(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        Player player = event.getEntity();
        if (gameManager.isInGame(player)) {
            // Запоминаем место смерти
            Location deathLocation = player.getLocation();

            // Откладываем возрождение на 1 тик (чтобы избежать багов)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.spigot().respawn(); // Принудительное возрождение
                player.teleport(deathLocation); // Телепортируем на место смерти
                player.setGameMode(GameMode.SPECTATOR); // Устанавливаем режим наблюдателя
            }, 1L);

            Player killer = player.getKiller();
            if (killer != null && gameManager.isInGame(killer)) {
                gameManager.endGame(killer);
            } else {
                gameManager.endGame(null);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        if (gameManager.isInGame(player)) {
            gameManager.removePlayer(player);
        }
    }
}