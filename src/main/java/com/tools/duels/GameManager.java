package com.tools.duels;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    private final JavaPlugin plugin;
    private final ArenaManager arenaManager;
    private final ScoreboardManager scoreboardManager;
    private List<Player> players = new ArrayList<>();
    private Map<Player, Player> opponents = new HashMap<>();
    private boolean gameStarted = false;
    private BossBar bossBar;
    private int countdownTaskId;
    private int gameTimerTaskId;
    private int healthUpdateTaskId;
    private int arenaCloseTaskId;
    private final FileConfiguration config;

    public GameManager(JavaPlugin plugin, ArenaManager arenaManager, ScoreboardManager scoreboardManager, FileConfiguration config) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.scoreboardManager = scoreboardManager;
        this.config = config;
        this.bossBar = Bukkit.createBossBar("Осталось времени: 1:00", BarColor.RED, BarStyle.SOLID);
    }

    public void addPlayer(Player player) {
        if (gameStarted || players.contains(player)) return;

        players.add(player);
        player.teleport(arenaManager.getArenaSpawn());
        player.setGameMode(GameMode.ADVENTURE);

        scoreboardManager.updateLobbyScoreboard(players, "Ожидание");
        Bukkit.broadcastMessage("§aИгрок " + player.getName() + " зашёл в дуэль (" + players.size() + "/2)");

        if (players.size() == 2) {
            startCountdown();
        }
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) return;

        players.remove(player);
        Bukkit.broadcastMessage("§cИгрок " + player.getName() + " вышел из дуэли (" + players.size() + "/2)");

        if (gameStarted) {
            endGame(null);
        } else {
            Bukkit.getScheduler().cancelTask(countdownTaskId);
            scoreboardManager.updateLobbyScoreboard(players, "Ожидание");
        }
    }

    private void updateScoreboardsForAll(String stage) {
        for (Player player : players) {
            if (gameStarted) {
                Player opponent = opponents.get(player);
                if (opponent != null) {
                    scoreboardManager.updateGameScoreboard(player, opponent, stage);
                }
            } else {
                scoreboardManager.updateLobbyScoreboard(players, stage);
            }
        }
    }

    private void startCountdown() {
        updateScoreboardsForAll("Подготовка к игре");
        countdownTaskId = new BukkitRunnable() {
            int timeLeft = config.getInt("game.countdown", 5);

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    for (Player player : players) {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    startGame();
                    cancel();
                    return;
                }

                updateScoreboardsForAll("Начало отсчёта");
                for (Player player : players) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, getPitchForCountdown(timeLeft));
                    player.sendTitle("§e" + timeLeft, "Дуэль начинается!", 0, 20, 0);
                    player.sendMessage("§eДуэль начинается через " + timeLeft + " секунд!");
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20).getTaskId();
    }

    private float getPitchForCountdown(int secondsLeft) {
        // Меняем тон звука в зависимости от оставшегося времени
        switch (secondsLeft) {
            case 5: return 0.5f;
            case 4: return 0.6f;
            case 3: return 0.7f;
            case 2: return 0.8f;
            case 1: return 1.0f;
            default: return 0.5f;
        }
    }

    private void startGame() {
        gameStarted = true;
        opponents.put(players.get(0), players.get(1));
        opponents.put(players.get(1), players.get(0));
        players.get(0).teleport(arenaManager.getPlayer1Spawn());
        players.get(1).teleport(arenaManager.getPlayer2Spawn());

        for (Player player : players) {
            preparePlayer(player);
            bossBar.addPlayer(player);
        }
        updateScoreboardsForAll("Сражение");

        startGameTimer();
        startHealthUpdateTask();
    }

    private void preparePlayer(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.clear();

        inv.setHelmet(new ItemStack(Material.IRON_HELMET));
        inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inv.setBoots(new ItemStack(Material.IRON_BOOTS));
        inv.setItem(0, new ItemStack(Material.IRON_SWORD));

        player.setHealth(20);
        player.setFoodLevel(20);
    }

    private void startGameTimer() {
        gameTimerTaskId = new BukkitRunnable() {
            int timeLeft = config.getInt("game.duration", 60);

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    endGame(null);
                    cancel();
                    return;
                }

                bossBar.setTitle("Осталось времени: " + timeLeft + "с");
                bossBar.setProgress(timeLeft / 60.0);

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20).getTaskId();
    }

    private void startHealthUpdateTask() {
        healthUpdateTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameStarted) {
                    cancel();
                    return;
                }

                for (Player player : players) {
                    Player opponent = opponents.get(player);
                    if (opponent != null) {
                        scoreboardManager.updateGameScoreboard(player, opponent, "Сражение");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10).getTaskId();
    }
    private void spawnFireworks(Player player) {
        // Запускаем 3 фейерверка для зрелищности
        for (int i = 0; i < 3; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta fwm = fw.getFireworkMeta();

                // Создаём эффект фейерверка
                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.withTrail().withFlicker().withFade(Color.ORANGE, Color.RED, Color.YELLOW);
                builder.with(FireworkEffect.Type.BALL_LARGE);
                builder.withColor(Color.PURPLE, Color.WHITE, Color.BLUE);

                fwm.addEffect(builder.build());
                fwm.setPower(1);
                fw.setFireworkMeta(fwm);

                // Удаляем фейерверк через 3 секунды
                Bukkit.getScheduler().runTaskLater(plugin, fw::remove, 20 * 3);
            }, 10 * i); // Запускаем с задержкой между фейерверками
        }
    }

    public void endGame(Player winner) {
        int countdown = 10;
        if (!gameStarted) return;

        updateScoreboardsForAll("Завершение игры");

        if (winner != null) {
            Bukkit.broadcastMessage("§6" + winner.getName() + " выиграл!");
            spawnFireworks(winner);
            for (Player player : players) {
                player.sendTitle("§6" + winner.getName() + " выиграл!", "", 10, 70, 20);
            }
        } else {
            Bukkit.broadcastMessage("§cВремя вышло! Ничья!");
            for (Player player : players) {
                player.sendTitle("§cВремя вышло!", "Ничья!", 10, 70, 20);
            }
        }
        Bukkit.broadcastMessage("§cАрена закроется через §e" + countdown + "§c секунд.");

        // Не очищаем скорборд, оставляем его с сообщением о завершении
        for (Player player : players) {
            player.getInventory().clear();
            bossBar.removePlayer(player);
        }

        players.clear();
        opponents.clear();
        gameStarted = false;

        Bukkit.getScheduler().cancelTask(countdownTaskId);
        Bukkit.getScheduler().cancelTask(gameTimerTaskId);
        Bukkit.getScheduler().cancelTask(healthUpdateTaskId);

        startArenaCloseTimer();
    }

    private void startArenaCloseTimer() {
        arenaCloseTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(arenaManager.getArenaSpawn().getWorld())) {
                        player.kickPlayer("§cАрена закрывается.");
                    }
                }
            }
        }.runTaskLater(plugin, 20 * 10).getTaskId(); // 10 секунд
    }

    public boolean isInGame(Player player) {
        return players.contains(player);
    }

    public Player getOpponent(Player player) {
        return opponents.get(player);
    }
}