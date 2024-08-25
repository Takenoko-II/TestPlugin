package com.gmail.subnokoii78.util.ui;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainerUI {
    private final Inventory inventory;

    private final Map<Integer, ItemButton> buttons = new HashMap<>();

    public ContainerUI(@NotNull TextComponent name, int lines) {
        inventory = Bukkit.createInventory(null, lines * 9, name);
        containerUISet.add(this);
    }

    public @NotNull ContainerUI set(int slot, @Nullable ItemButton button) {
        if (inventory.getSize() <= slot) {
            throw new IllegalArgumentException();
        }

        if (button == null) inventory.clear(slot);
        else {
            inventory.setItem(slot, button.build());
            buttons.put(slot, button);
        }
        return this;
    }

    public @NotNull ContainerUI add(@NotNull ItemButton button) {
        if (inventory.firstEmpty() == -1) {
            throw new IllegalStateException();
        }

        buttons.put(inventory.firstEmpty(), button);
        inventory.addItem(button.build());
        return this;
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    private static final Set<ContainerUI> containerUISet = new HashSet<>();

    public static final class UIEventHandler implements Listener {
        private UIEventHandler() {}

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final ItemStack itemStack = event.getCurrentItem();

            for (final ContainerUI ui : containerUISet) {
                if (!ui.inventory.equals(event.getClickedInventory())) continue;

                final ItemButton button = ui.buttons.get(event.getSlot());

                if (itemStack == null || button == null) return;

                button.click(new ItemButtonClickEvent(player, button));
                event.setCancelled(true);
            }
        }

        private static final UIEventHandler instance = new UIEventHandler();

        private static boolean initialized = false;

        public static void init() {
            if (initialized) return;
            initialized = true;
            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.getInstance());
        }
    }
}
