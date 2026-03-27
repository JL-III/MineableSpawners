package com.dnyferguson.mineablespawners.commands;

import com.dnyferguson.mineablespawners.MineableSpawners;
import com.dnyferguson.mineablespawners.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class GiveSubCommand {

    public GiveSubCommand() {}

    public void execute(MineableSpawners plugin, CommandSender sender, String target, String type, String amt) {
        Player targetPlayer = Bukkit.getPlayer(target);
        if (target == null || targetPlayer == null) {
            plugin.getConfigurationHandler().sendMessage("give", "player-does-not-exist", sender);
            return;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getConfigurationHandler().sendMessage("give", "invalid-type", sender);
            return;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(amt);
        } catch (NumberFormatException e) {
            plugin.getConfigurationHandler().sendMessage("give", "invalid-amount", sender);
            return;
        }

        ItemStack item = MineableSpawners.getApi().getSpawnerFromEntityType(entityType);
        item.setAmount(amount);

        String mobFormatted = Chat.uppercaseStartingLetters(entityType.name());

        if (targetPlayer.getInventory().firstEmpty() == -1) {
            if (!plugin.getConfigurationHandler().getBooleanOrDefault("give", "drop-if-full", true)) {
                plugin.getConfigurationHandler().sendMessage("give", "inventory-full", sender);
                return;
            }

            plugin.getLogger().log(Level.INFO, "Dropped " + amount + "x " + mobFormatted + " Spawners at " + targetPlayer.getName() + "'s feet since their inventory was full!");
            targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), item);

            return;
        }

        targetPlayer.getInventory().addItem(item);
        plugin.getConfigurationHandler().getMessage("give", "success").replace("%mob%", mobFormatted).replace("%target%", targetPlayer.getName()).replace("%amount%", amount + "");
        targetPlayer.sendMessage(plugin.getConfigurationHandler().getMessage("give", "received").replace("%mob%", mobFormatted).replace("%target%", targetPlayer.getName()).replace("%amount%", amount + ""));
    }
}
