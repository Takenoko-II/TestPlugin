package com.gmail.subnokoii78.util.event.data;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class PlayerClickEvent {
    private final Player player;

    private final ItemStack itemStack;

    private final Cancellable event;

    public PlayerClickEvent(PlayerInteractEvent event) {
        player = event.getPlayer();
        itemStack = Objects.requireNonNullElse(event.getItem(), new ItemStack(Material.AIR));
        this.event = event;
    }

    public PlayerClickEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getDamager();

        if (!(entity instanceof Player)) throw new RuntimeException();

        player = (Player) entity;

        itemStack = player.getEquipment().getItemInMainHand();
        this.event = event;
    }

    public PlayerClickEvent(PrePlayerAttackEntityEvent event) {
        player = event.getPlayer();
        itemStack = player.getEquipment().getItemInMainHand();
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void cancel() {
        event.setCancelled(true);
    }
}
