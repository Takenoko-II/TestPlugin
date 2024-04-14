package com.gmail.subnokoii.testplugin.lib.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChestUIClickEventListener implements Listener {
    @EventHandler
    public void on(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack itemStack = event.getCurrentItem();

        for (final ChestUIBuilder ui : ChestUIBuilder.getAll()) {
            if (ui.getInventory().equals(event.getClickedInventory())) {
                for (final ChestUIButtonBuilder button : ui.getAllButtons()) {
                    if (button == null) continue;
                    if (button.getItemStack().equals(itemStack)) {
                        button.click(player);
                        ui.set(event.getSlot(), (ignored) -> button);
                        event.setCancelled(true);
                        break;
                    }
                }
                break;
            }
        }
    }
}
