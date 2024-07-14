package com.gmail.subnokoii78.testplugin.util.event.data;

import com.gmail.subnokoii78.testplugin.util.datacontainer.ItemStackDataContainerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemUseEvent {
    private PlayerInteractEvent playerInteractEvent;

    private EntityDamageByEntityEvent entityDamageByEntityEvent;

    private final Player player;

    private final ItemStack itemStack;

    private final String tag;

    private final boolean isLeftClick;

    public CustomItemUseEvent(PlayerInteractEvent event, boolean isLeftClick) {
        playerInteractEvent = event;
        player = event.getPlayer();
        itemStack = event.getItem();
        tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");
        this.isLeftClick = isLeftClick;

        if (event.getItem() == null || tag == null) {
            throw new RuntimeException();
        }
    }

    public CustomItemUseEvent(EntityDamageByEntityEvent event) {
        entityDamageByEntityEvent = event;
        final Entity damagingEntity = event.getDamager();

        if (!(damagingEntity instanceof Player)) {
            throw new RuntimeException();
        }

        player = (Player) damagingEntity;
        itemStack = player.getEquipment().getItemInMainHand();
        tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");
        isLeftClick = true;

        if (tag == null) {
            throw new RuntimeException();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getTag() {
        return tag;
    }

    public boolean isLeftClick() {
        return isLeftClick;
    }

    public boolean isRightClick() {
        return !isLeftClick;
    }

    public void cancel() {
        if (entityDamageByEntityEvent != null) {
            entityDamageByEntityEvent.setCancelled(true);
        }
        else if (playerInteractEvent != null) {
            playerInteractEvent.setCancelled(true);
        }
        else {
            throw new RuntimeException();
        }
    }
}
