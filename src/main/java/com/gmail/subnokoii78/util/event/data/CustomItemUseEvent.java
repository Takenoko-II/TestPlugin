package com.gmail.subnokoii78.util.event.data;

import com.gmail.subnokoii78.util.datacontainer.ItemStackDataContainerManager;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CustomItemUseEvent {
    private PlayerInteractEvent playerInteractEvent;

    private EntityDamageByEntityEvent entityDamageByEntityEvent;

    private PrePlayerAttackEntityEvent prePlayerAttackEntityEvent;

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

    public CustomItemUseEvent(PrePlayerAttackEntityEvent event) {
        prePlayerAttackEntityEvent = event;
        player = event.getPlayer();
        itemStack = player.getEquipment().getItemInMainHand();
        tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");
        isLeftClick = true;
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
        else if (prePlayerAttackEntityEvent != null) {
            prePlayerAttackEntityEvent.setCancelled(true);
        }
        else {
            throw new RuntimeException();
        }
    }
}
