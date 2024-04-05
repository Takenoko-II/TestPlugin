package com.gmail.subnokoii.testplugin.lib.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChestUIClickEventListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final ItemStack itemStack = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();

        for (final ChestUIBuilder builder : ChestUIBuilder.getBuilders()) {
            if (builder.getInventory().equals(event.getInventory())) {
                for (final ChestUIButtonBuilder itemStackBuilder : builder.getItemStacks()) {
                    if (itemStackBuilder == null) continue;
                    if (!itemStackBuilder.getItemStack().equals(itemStack)) continue;

                    final ChestUIClickEvent clickEvent = new ChestUIClickEvent(player, itemStackBuilder);

                    itemStackBuilder.getConsumer().accept(clickEvent);
                    event.setCancelled(true);
                }
            }
        }
    }
}
