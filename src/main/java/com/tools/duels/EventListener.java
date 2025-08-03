package com.tools.duels;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    private final Main plugin;
    private final GameManager gameManager;
    private final ModeManager modeManager;

    public EventListener(Main plugin, GameManager gameManager, ModeManager modeManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.modeManager = modeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        gameManager.addPlayer(player);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        modeManager.selectMode(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        Player player = event.getEntity();
        if (gameManager.isInGame(player)) {
            Location deathLocation = player.getLocation();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.spigot().respawn();
                player.teleport(deathLocation);
                player.setGameMode(GameMode.SPECTATOR);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.GLOWSTONE_DUST &&
                item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§c§lВыбор режима")) {
            modeManager.openGUI(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GOLD + "Выбор режима")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.IRON_SWORD) {
            modeManager.setMode(player, "§eClassic");
        } else if (clickedItem.getType() == Material.DIAMOND_SWORD) {
            modeManager.setMode(player, "§eOP");
        } else if (clickedItem.getType() == Material.SPLASH_POTION) {
            modeManager.setMode(player, "§eNoDebuff");
        }

    }

    @EventHandler
    public void blockThrow(PlayerDropItemEvent event) {
        // Блок выкидывания предметов
        event.setCancelled(true);
    }
}