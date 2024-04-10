package com.gmail.subnokoii.testplugin.lib.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChestUIClickEventListener implements Listener {
    @EventHandler
    public void on(InventoryClickEvent event) {
        final ItemStack itemStack = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();

        final List<ChestUIBuilder> buildersList = ChestUIBuilder.getBuilders();

        final ChestUIBuilder[] buildersArray = buildersList.toArray(new ChestUIBuilder[buildersList.size()]);

        for (final ChestUIBuilder builder : buildersArray) {
            if (!builder.getInventory().equals(event.getInventory())) continue;

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
