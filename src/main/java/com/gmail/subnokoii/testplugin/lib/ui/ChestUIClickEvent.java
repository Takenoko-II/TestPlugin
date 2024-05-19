package com.gmail.subnokoii.testplugin.lib.ui;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackDataContainerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChestUIClickEvent {
    private final Player player;

    private final ChestUIBuilder.Button button;

    public ChestUIClickEvent(Player player, ChestUIBuilder.Button button) {
        this.player = player;
        this.button = button;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getClickedItemStack() {
        final ItemStack itemStack = button.getItemStack().clone();

        new ItemStackDataContainerAccessor(itemStack).delete("id");

        return itemStack;
    }

    public void close() {
        player.closeInventory();
    }

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public boolean runCommand(String command) {
        return TestPlugin.runCommand(player, command);
    }

    public static final class Listener implements org.bukkit.event.Listener {
        private static Listener instance;

        public static Listener get() {
            if (instance == null) {
                throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
            }

            return instance;
        }

        public static void init() {
            if (instance == null) {
                instance = new Listener();
                Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.get());
            }
        }

        private Listener() {}

        @EventHandler
        public void on(InventoryClickEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null) return;

            for (final ChestUIBuilder ui : ChestUIBuilder.getAll()) {
                if (ui.getInventory().equals(event.getClickedInventory())) {
                    for (final ChestUIBuilder.Button button : ui.getAllButtons()) {
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
}
