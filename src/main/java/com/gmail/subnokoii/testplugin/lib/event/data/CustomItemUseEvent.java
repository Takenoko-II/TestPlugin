package com.gmail.subnokoii.testplugin.lib.event.data;

import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemUseEvent {
    private final PlayerInteractEvent event;

    private final Player player;

    private final ItemStack itemStack;

    private final String tag;

    private final boolean isLeftClick;

    public CustomItemUseEvent(PlayerInteractEvent event, boolean isLeftClick) {
        this.event = event;
        player = event.getPlayer();
        itemStack = event.getItem();
        tag = NBTEditor.getString(itemStack, "plugin", "custom_item_tag");
        this.isLeftClick = isLeftClick;

        if (itemStack == null || tag == null) {
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
        event.setCancelled(true);
    }
}
