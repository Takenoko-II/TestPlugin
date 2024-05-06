package com.gmail.subnokoii.testplugin.lib.event;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.event.data.CustomItemUseEvent;
import com.gmail.subnokoii.testplugin.lib.event.data.PlayerClickEvent;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.other.ScheduleUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class TestPluginEvent implements Listener {
    private static TestPluginEvent instance;

    public static TestPluginEvent get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new TestPluginEvent();
            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.get());
        }
    }

    public static final class EventRegisterer {
        public void onLeftClick(Consumer<PlayerClickEvent> listener) {
            TestPluginEvent.get().playerLeftClickEventListeners.add(listener);
        }

        public void onRightClick(Consumer<PlayerClickEvent> listener) {
            TestPluginEvent.get().playerRightClickEventListeners.add(listener);
        }

        public void onCustomItemUse(Consumer<CustomItemUseEvent> listener) {
            TestPluginEvent.get().customItemUseEventListeners.add(listener);
        }

        private EventRegisterer() {}

        private static EventRegisterer registerer;

        public static EventRegisterer get() {
            if (registerer == null) registerer = new EventRegisterer();

            return registerer;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        lastDroppedItemTimestamp.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final ItemStack itemStack = event.getItem();

        if (event.getAction().isRightClick()) {
            lastRightClickedTimestamp.put(event.getPlayer(), System.currentTimeMillis());

            playerRightClickEventListeners.forEach(listener -> {
                listener.accept(new PlayerClickEvent(event));
            });

            if (itemStack == null) return;

            final String tag = NBTEditor.getString(itemStack, "plugin", "custom_item_tag");

            if (tag == null) return;

            customItemUseEventListeners.forEach(listener -> {
                listener.accept(new CustomItemUseEvent(event, false));
            });
        }
        else {
            ScheduleUtils.runTimeout(() -> {
                final Player player = event.getPlayer();
                final long currentTime = System.currentTimeMillis();
                final long lastDroppedItemTime = Objects.requireNonNullElse(lastDroppedItemTimestamp.get(player), 0L);
                final long lastRightClickedTime = Objects.requireNonNullElse(lastRightClickedTimestamp.get(player), 0L);

                if (currentTime - lastDroppedItemTime < 50L) return;
                if (currentTime - lastRightClickedTime < 50L) return;

                ScheduleUtils.runTimeoutByGameTick(() -> {
                    leftClick(event);
                });

                if (itemStack == null) return;

                final String tag = NBTEditor.getString(itemStack, "plugin", "custom_item_tag");

                if (tag == null) return;

                ScheduleUtils.runTimeoutByGameTick(() -> {
                    customItemUseEventListeners.forEach(listener -> {
                        listener.accept(new CustomItemUseEvent(event, true));
                    });
                });
            }, 5L);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity damagingEntity = event.getDamager();

        if (!(damagingEntity instanceof Player)) return;

        final Entity entity = event.getEntity();
        final EntityDamageEvent.DamageCause cause = event.getCause();
        final long currentTime = System.currentTimeMillis();
        final long lastDamageByEntityTime = Objects.requireNonNullElse(lastDamageByEntityTimestamp.get(entity), 0L);

        lastDamageByEntityTimestamp.put(entity, currentTime);

        if (currentTime - lastDamageByEntityTime < 50L) return;

        if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && !event.getDamageSource().isIndirect()) {
            leftClick(event);
        }
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        lastDamageByEntityTimestamp.remove(event.getEntity());
    }

    private void leftClick(PlayerInteractEvent event) {
        playerLeftClickEventListeners.forEach(listener -> {
            listener.accept(new PlayerClickEvent(event));
        });
    }

    private void leftClick(EntityDamageByEntityEvent event) {
        playerLeftClickEventListeners.forEach(listener -> {
            listener.accept(new PlayerClickEvent(event));
        });
    }

    private final Set<Consumer<PlayerClickEvent>> playerLeftClickEventListeners = new HashSet<>();

    private final Set<Consumer<PlayerClickEvent>> playerRightClickEventListeners = new HashSet<>();

    private final Set<Consumer<CustomItemUseEvent>> customItemUseEventListeners = new HashSet<>();

    private final Map<Player, Long> lastRightClickedTimestamp = new HashMap<>();

    private final Map<Entity, Long> lastDamageByEntityTimestamp = new HashMap<>();

    private final Map<Player, Long> lastDroppedItemTimestamp = new HashMap<>();
}
