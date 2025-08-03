package com.tools.duels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModeManager {
    private final Map<Player, String> selectedModes = new HashMap<>();
    private Main plugin;

    public ModeManager(Main plugin) {
        this.plugin = plugin;
    }

    public void selectMode(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.clear();

        ItemStack selector = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = selector.getItemMeta();
        meta.setDisplayName("§c§lВыбор режима");
        meta.setLore(Arrays.asList(ChatColor.GOLD + "ПКМ чтобы открыть"));
        selector.setItemMeta(meta);
        player.getInventory().setItem(8, selector);
    }

    public void openGUI (Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Выбор режима");

        ItemStack classic = createGuiItem(Material.IRON_SWORD, ChatColor.WHITE + "Classic", "§eСамый классический режим", "§eЖелезный сет брони");
        ItemStack op = createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "OP", "§eOP режим", "§eАлмазный сет брони, геплы");
        ItemStack nodebuff = createGuiItem(Material.SPLASH_POTION, ChatColor.GOLD + "NoDebuff", "§eТот самый NoDebuff", "§eAKA поты. AKA зельки", "§eАлмазный сет брони, 28 зелий лечения");
        ItemStack sw = createGuiItem(Material.ENDER_PEARL, ChatColor.AQUA + "SkyWars", "§9В разработке");
        ItemStack bw = createGuiItem(Material.RED_BED, ChatColor.RED + "BedWars", "§9В разработке");

        gui.setItem(0, classic);
        gui.setItem(2, op);
        gui.setItem(4, nodebuff);
        gui.setItem(6, sw);
        gui.setItem(8, bw);

        player.openInventory(gui);

    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public void setMode(Player player, String mode) {
        selectedModes.put(player, mode);
        Bukkit.broadcastMessage("§a" + player.getName() + " выбрал " + mode + "§a режим ");
        checkModes();
    }

    private void checkModes() {
        GameManager gameManager = plugin.getGameManager();
        if (gameManager.getPlayers().size() == 2) {
            Player player1 = gameManager.getPlayers().get(0);
            Player player2 = gameManager.getPlayers().get(1);

            String mode1 = selectedModes.get(player1);
            String mode2 = selectedModes.get(player2);

            if (mode1 != null && mode2 != null && mode1.equals(mode2)) {
                Bukkit.broadcastMessage("§6Выбран режим: " + mode2);
                player1.getInventory().clear();
                player1.closeInventory();
                player2.getInventory().clear();
                player2.closeInventory();
                gameManager.startCountdown();
            }
        }
    }

    public void getReady(Player player) {
        if (selectedModes.get(plugin.getGameManager().getPlayers().get(1)).equals("§eClassic")) {
            classic(player);

        } else if (selectedModes.get(plugin.getGameManager().getPlayers().get(1)).equals("§eOP")) {
            op(player);

        } else if (selectedModes.get(plugin.getGameManager().getPlayers().get(1)).equals("§eNoDebuff")) {
            nodebuff(player);
        }
        player.setHealth(20);
        player.setFoodLevel(20);

    }

    public void classic(Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
    }

    public void op(Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);

        player.getInventory().addItem(sword);

        player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4);

        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 6));

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) speedPotion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 0), true);
        speedPotion.setItemMeta(meta);

        ItemStack regenPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta2 = (PotionMeta) regenPotion.getItemMeta();
        meta2.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 1800, 0), true);
        regenPotion.setItemMeta(meta2);

        player.getInventory().addItem(speedPotion, regenPotion);
    }

    public void nodebuff(Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
        player.getInventory().addItem(sword);

        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 5));

        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 20, 1), true);
        potion.setItemMeta(meta);

        ItemStack potion2 = new ItemStack(Material.POTION);
        PotionMeta meta2 = (PotionMeta) potion2.getItemMeta();
        meta2.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 0), true);
        potion2.setItemMeta(meta2);

        ItemStack potion3 = new ItemStack(Material.POTION, 4);
        PotionMeta meta3 = (PotionMeta) potion3.getItemMeta();
        meta3.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 0), true);
        potion3.setItemMeta(meta3);

        player.getInventory().addItem(potion2, potion3);

        for (int i = 0; i < 27; i++) {
            player.getInventory().addItem(potion);
        }
    }

}
