package com.gmail.subnokoii78.util.ui;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.datacontainer.ItemStackDataContainerManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ChestUIClickEvent {
    private final Player player;

    private final ItemStack itemStack;

    public ChestUIClickEvent(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getClickedItemStack() {
        final ItemStack itemStack = this.itemStack.clone();

        new ItemStackDataContainerManager(itemStack).delete("id");

        return itemStack;
    }

    public void close() {
        player.closeInventory();
    }

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public boolean runCommand(String command) {
        return TestPlugin.runCommandAsEntity(player, command);
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
                Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.getInstance());
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

                        if (button.match(itemStack)) {
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

        @EventHandler
        public void onMove(InventoryMoveItemEvent event) {
            final Inventory src = event.getSource();
            final Inventory dest = event.getDestination();
            final ItemStack itemStack = event.getItem();

            for (final ChestUIBuilder ui : ChestUIBuilder.getAll()) {
                if (ui.getInventory().equals(dest)) {
                    event.setCancelled(true);
                    break;
                }
                else if (ui.getInventory().equals(src) && dest instanceof PlayerInventory playerInventory) {
                    if (playerInventory.getItemInOffHand().equals(itemStack)) {
                        for (final ChestUIBuilder.Button button : ui.getAllButtons()) {
                            if (button.match(itemStack)) {
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
}
