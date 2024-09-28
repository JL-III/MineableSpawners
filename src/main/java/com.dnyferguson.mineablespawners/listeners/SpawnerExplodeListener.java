package com.dnyferguson.mineablespawners.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.dnyferguson.mineablespawners.MineableSpawners;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SpawnerExplodeListener implements Listener {
    private final MineableSpawners plugin;

    public SpawnerExplodeListener(MineableSpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawnerExplode(EntityExplodeEvent e) {
        if (!plugin.getConfigurationHandler().getBoolean("explode", "drop")) {
            return;
        }

        if (e.getLocation().getWorld() != null && plugin.getConfigurationHandler().getList("explode", "blacklisted-worlds").contains(e.getLocation().getWorld().getName())) {
            return;
        }

        for (Block block : e.blockList()) {
            if (!block.getType().equals(XMaterial.SPAWNER.parseMaterial())) {
                continue;
            }

            double dropChance = plugin.getConfigurationHandler().getDouble("explode", "chance")/100;

            if (dropChance != 1) {
                double random = Math.random();
                if (random >= dropChance) {
                    return;
                }
            }

            CreatureSpawner spawner = (CreatureSpawner) block.getState();

            ItemStack item = MineableSpawners.getApi().getSpawnerFromEntityType(Objects.requireNonNull(spawner.getSpawnedType()));

            if (block.getLocation().getWorld() != null) {
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
            }
        }
    }
}
