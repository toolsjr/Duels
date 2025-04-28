package com.tools.duels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    private final Main plugin;
    private final GameManager gameManager;

    public CommandManager(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§eКоманды дуэлей:");
            player.sendMessage("§a/duels join - Войти в дуэль");
            player.sendMessage("§a/duels leave - Покинуть дуэль");
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            gameManager.addPlayer(player);
            return true;
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (gameManager.isInGame(player)) {
                gameManager.removePlayer(player);
                player.sendMessage("§aВы покинули дуэль.");
            } else {
                player.sendMessage("§cВы не участвуете в дуэли!");
            }
            return true;
        }

        player.sendMessage("§cНеизвестная команда. Используйте /duels для справки.");
        return false;
    }
}