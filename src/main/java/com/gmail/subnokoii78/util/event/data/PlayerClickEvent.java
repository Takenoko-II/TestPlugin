package com.gmail.subnokoii78.util.event.data;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class PlayerClickEvent {
    private final Player player;

    private final ItemStack itemStack;

    public PlayerClickEvent(PlayerInteractEvent event) {
        player = event.getPlayer();
        itemStack = Objects.requireNonNullElse(event.getItem(), new ItemStack(Material.AIR));
    }

    public PlayerClickEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getDamager();

        if (!(entity instanceof Player)) throw new RuntimeException();

        player = (Player) entity;

        itemStack = player.getEquipment().getItemInMainHand();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
