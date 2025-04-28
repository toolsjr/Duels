package com.tools.duels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardManager {
    private final JavaPlugin plugin;

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void updateLobbyScoreboard(List<Player> players, String stage) {
        for (Player player : players) {

            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("duels", "dummy", ChatColor.GOLD + "Дуэли");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            objective.getScore(ChatColor.GRAY + stage).setScore(3);
            objective.getScore(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Classic").setScore(2);
            objective.getScore(ChatColor.GRAY + "Игроки: " + ChatColor.WHITE + players.size() + "/2").setScore(1);

            player.setScoreboard(board);
        }
    }

    public void updateGameScoreboard(Player player, Player opponent, String stage) {
        if (player == null || opponent == null || !player.isOnline()) return;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("duels", "dummy", ChatColor.GOLD + "Дуэли");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(ChatColor.GRAY + stage).setScore(4);
        objective.getScore(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Classic").setScore(3);
        objective.getScore(ChatColor.GRAY + "Оппонент: " + ChatColor.WHITE + opponent.getName()).setScore(2);
        objective.getScore(ChatColor.GRAY + "Здоровье: " + ChatColor.RED + (int) opponent.getHealth() + "❤").setScore(1);

        player.setScoreboard(board);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}