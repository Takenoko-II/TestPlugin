package com.gmail.subnokoii78.testplugin.util.event;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.util.event.data.CustomItemUseEvent;
import com.gmail.subnokoii78.testplugin.util.event.data.DataPackMessageReceiveEvent;
import com.gmail.subnokoii78.testplugin.util.event.data.PlayerClickEvent;
import com.gmail.subnokoii78.testplugin.util.datacontainer.ItemStackDataContainerManager;
import com.gmail.subnokoii78.testplugin.util.other.ScheduleUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class CustomEvents implements Listener {
    private static CustomEvents instance;

    private static CustomEvents listener() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new CustomEvents();
            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.getInstance());
        }
    }

    public static final class Registrar {
        public void onLeftClick(Consumer<PlayerClickEvent> listener) {
            CustomEvents.listener().playerLeftClickEventListeners.add(listener);
        }

        public void onRightClick(Consumer<PlayerClickEvent> listener) {
            CustomEvents.listener().playerRightClickEventListeners.add(listener);
        }

        public void onCustomItemUse(Consumer<CustomItemUseEvent> listener) {
            CustomEvents.listener().customItemUseEventListeners.add(listener);
        }

        public void onDataPackMessageReceive(Consumer<DataPackMessageReceiveEvent> listener) {
            CustomEvents.listener().dataPackMessageReceiveEventListeners.add(listener);
        }

        public Registrar() {}
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        lastDroppedItemTimestamp.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final ItemStack itemStack = event.getItem();

        lastInteractedTimestamp.put(event.getPlayer(), System.currentTimeMillis());

        if (event.getAction().isRightClick()) {
            lastRightClickedTimestamp.put(event.getPlayer(), System.currentTimeMillis());

            playerRightClickEventListeners.forEach(listener -> {
                listener.accept(new PlayerClickEvent(event));
            });

            if (itemStack == null) return;

            final String tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");

            if (tag == null) return;

            customItemUseEventListeners.forEach(listener -> {
                listener.accept(new CustomItemUseEvent(event, false));
            });
        }
        else {
            ScheduleUtils.runTimeout(() -> {
                final Player player = event.getPlayer();
                final long currentTime = System.currentTimeMillis();
                final long lastAttackedTime = Objects.requireNonNullElse(lastAttackedTimestamp.get(player), 0L);
                final long lastDroppedItemTime = Objects.requireNonNullElse(lastDroppedItemTimestamp.get(player), 0L);
                final long lastRightClickedTime = Objects.requireNonNullElse(lastRightClickedTimestamp.get(player), 0L);

                if (currentTime - lastAttackedTime < 50L) return;
                if (currentTime - lastDroppedItemTime < 50L) return;
                if (currentTime - lastRightClickedTime < 50L) return;

                ScheduleUtils.runTimeoutByGameTick(() -> {
                    leftClick(event);
                });

                if (itemStack == null) return;

                final String tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");

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
        final Player player = (Player) damagingEntity;

        final EntityDamageEvent.DamageCause cause = event.getCause();
        final long currentTime = System.currentTimeMillis();
        final long lastDamageByEntityTime = Objects.requireNonNullElse(lastDamageByEntityTimestamp.get(entity), 0L);

        lastDamageByEntityTimestamp.put(entity, currentTime);
        lastAttackedTimestamp.put(player, currentTime);

        if (currentTime - lastDamageByEntityTime < 50L) return;

        if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && !event.getDamageSource().isIndirect()) {
            leftClick(event);

            final ItemStack itemStack = player.getEquipment().getItemInMainHand();

            final String tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");

            if (tag == null) return;

            final long lastInteract = Objects.requireNonNullElse(lastInteractedTimestamp.get(player), 0L);

            if (currentTime - lastInteract < 50L) return;

            customItemUseEventListeners.forEach(listener -> {
                listener.accept(new CustomItemUseEvent(event));
            });
        }
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        lastDamageByEntityTimestamp.remove(event.getEntity());
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.getType().equals(EntityType.MARKER)) return;

        final Set<String> tags = entity.getScoreboardTags();
        final String namespace = TestPlugin.getInstance().getName().toLowerCase() + ":";

        tags.forEach(tag -> {
            if (tag.startsWith(namespace)) {
                final String message = tag.split(namespace, 2)[1];

                final Entity[] entities = entity.getWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getScoreboardTags().contains("plugin_api.target"))
                .toArray(Entity[]::new);

                onDataPackMessageReceive(entity, entities, message.split("\\s+"));
            }
        });
    }

    private void onDataPackMessageReceive(Entity entity, Entity[] targets, String[] message) {
        dataPackMessageReceiveEventListeners.forEach(listener -> {
            listener.accept(new DataPackMessageReceiveEvent(entity, targets, message));
        });
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

    private final Set<Consumer<DataPackMessageReceiveEvent>> dataPackMessageReceiveEventListeners = new HashSet<>();

    private final Map<Player, Long> lastInteractedTimestamp = new HashMap<>();

    private final Map<Player, Long> lastRightClickedTimestamp = new HashMap<>();

    private final Map<Entity, Long> lastDamageByEntityTimestamp = new HashMap<>();

    private final Map<Player, Long> lastAttackedTimestamp = new HashMap<>();

    private final Map<Player, Long> lastDroppedItemTimestamp = new HashMap<>();
}
