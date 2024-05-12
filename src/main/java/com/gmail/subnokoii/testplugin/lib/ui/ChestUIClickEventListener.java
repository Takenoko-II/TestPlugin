package com.gmail.subnokoii.testplugin.lib.ui;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChestUIClickEventListener implements Listener {
    private static ChestUIClickEventListener instance;

    public static ChestUIClickEventListener get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new ChestUIClickEventListener();
            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.get());
        }
    }

    private ChestUIClickEventListener() {}

    @EventHandler
    public void on(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        for (final ChestUIBuilder ui : ChestUIBuilder.getAll()) {
            if (ui.getInventory().equals(event.getClickedInventory())) {
                for (final ChestUIButtonBuilder button : ui.getAllButtons()) {
                    if (button == null) continue;

                    if (button.matchId(itemStack)) {
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