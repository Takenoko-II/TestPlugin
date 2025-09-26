package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.PlayerClickEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.shape.Shape;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonByte;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

public class PlayerEventListener implements Listener {
    private PlayerEventListener() {}

    private static PlayerEventListener instance;

    public static PlayerEventListener get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new PlayerEventListener();

            Bukkit.getServer().getPluginManager().registerEvents(instance, TPLCore.getPlugin());

            registerPluginEvents();
        }
    }

    private static void registerPluginEvents() {
        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, event -> {
            if (!event.getClick().equals(PlayerClickEvent.Click.LEFT)) {
                return;
            }

            final Player player = event.getPlayer();
            final ItemStack itemStack = player.getEquipment().getItem(EquipmentSlot.HAND);

            if (itemStack.getItemMeta() == null) return;

            final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

            final String tag = compound.has("custom_item_tag")
                ? compound.get("custom_item_tag", MojangsonValueTypes.STRING).getValue()
                : null;

            if (tag != null) {
                switch (tag) {
                    case "instant_shoot_bow": {
                        final int count = Objects.requireNonNullElse(shootableCountByLeftClick.get(player), 0);

                        if (count <= 0) {
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 1f);
                            break;
                        }

                        shootableCountByLeftClick.put(player, count - 1);

                        final Vector3Builder vector = DualAxisRotationBuilder.from(player.getLocation())
                            .getDirection3d();

                        final Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), vector.toBukkitVector(), 1.6f, 12f);
                        arrow.setCritical(true);
                        arrow.setDamage(4d);
                        arrow.setLifetimeTicks(1200);
                        arrow.setShooter(player);
                        arrow.setHasLeftShooter(false);

                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 5f, 1f);
                        break;
                    }
                }
            }

            if (player.getScoreboardTags().contains("plugin_api.disable_left_click")) {
                event.cancel();
            }

            TPLCore.getScoreboard()
                .getOrAddObjective("plugin_api.on_left_click", null, null, null)
                .addScore(player, 1);

            final MojangsonCompound compoundL = ItemStackCustomDataAccess.of(itemStack).read();

            final String type = compoundL.has(MojangsonPath.of("on_left_click.type"))
                ? compoundL.get(MojangsonPath.of("on_left_click.type"), MojangsonValueTypes.STRING).getValue()
                : null;
            final String content = compoundL.has(MojangsonPath.of("on_left_click.content"))
                ? compoundL.get(MojangsonPath.of("on_left_click.content"), MojangsonValueTypes.STRING).getValue()
                : null;

            if (type != null && content != null) {
                switch (type) {
                    case "run_command":
                        new Execute(new CommandSourceStack(player))
                            .run.command(content);
                        break;
                    case "multiple":
                        break;
                }
            }
        });

        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, event -> {
            final Player player = event.getPlayer();
            TPLCore.getScoreboard()
                .getOrAddObjective("plugin_api.on_right_click", null, null, null)
                .addScore(player, 1);

            final ItemStack itemStack = player.getEquipment().getItem(EquipmentSlot.HAND);
            if (itemStack.getItemMeta() == null) return;

            final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

            final String type = compound.has(MojangsonPath.of("on_right_click.type"))
                ? compound.get(MojangsonPath.of("on_right_click.type"), MojangsonValueTypes.STRING).getValue()
                : null;
            final String content = compound.has(MojangsonPath.of("on_right_click.content"))
                ? compound.get(MojangsonPath.of("on_right_click.content"), MojangsonValueTypes.STRING).getValue()
                : null;

            if (type != null && content != null) {
                switch (type) {
                    case "run_command":
                        new Execute(new CommandSourceStack(player))
                            .run.command(content);
                        break;
                }
            }
        });

        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, event -> {
            final Player player = event.getPlayer();

            final ItemStack itemStack = player.getEquipment().getItem(EquipmentSlot.HAND);

            if (itemStack.getItemMeta() == null) return;

            final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

            switch (compound.has("custom_item_tag") ? compound.get("custom_item_tag", MojangsonValueTypes.STRING).getValue() : null) {
                case "quick_teleporter": {
                    event.cancel();

                    final Block block = player.getTargetBlockExact(127);
                    final BlockFace face = player.getTargetBlockFace(127);

                    if (block == null || face == null) break;

                    final Vector3Builder direction = DualAxisRotationBuilder.from(player).getDirection3d();

                    final Location destination = Vector3Builder.from(block, face)
                        .subtract(direction.length(0.5d))
                        .withLocation(player.getLocation());

                    player.teleport(destination);
                    player.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 10.0f, 2.0f);
                    player.getWorld().spawnParticle(Particle.PORTAL, destination, 40, 0.5d, 1.0d, 0.5d);

                    break;
                }
                case "data_getter": {
                    event.cancel();

                    final Entity entity = player.getTargetEntity(16);
                    final Block block = player.getTargetBlockExact(16);

                    if (entity != null) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.ENCHANT, entity.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        player.sendMessage(Component.text(entity.getType().name() + "は以下のNBTを持っています:\n" + entity.getAsString()));

                    }
                    else if (block != null) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.ENCHANT, block.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        player.sendMessage(Component.text(block.getType().name() + "は以下のデータを持っています:\n" + block.getBlockData().getAsString()));
                    }

                    break;
                }
                case "tick_progress_canceler": {
                    event.cancel();

                    final ServerTickManager manager = player.getServer().getServerTickManager();
                    final boolean isFrozen = manager.isFrozen();

                    if (isFrozen) {
                        manager.setFrozen(false);
                        player.getServer().getOnlinePlayers().forEach(p -> {
                            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10.0f, 2.0f);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 127, false, false));
                        });
                    }
                    else {
                        manager.setFrozen(true);
                        player.getServer().getOnlinePlayers().forEach(p -> {
                            p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 10.0f, 0.8f);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 127, false, false));
                        });
                    }
                    break;
                }
                case "server_selector": {
                    event.cancel();
                    TPLCore.paperVelocityManager.getServerSelectorInteraction().open(player);
                    break;
                }
                case "magic": {
                    final Vector3Builder.LocalAxisProvider localAxes = DualAxisRotationBuilder.from(player).getDirection3d().getLocalAxisProvider();
                    final Vector3Builder x = localAxes.getX().length(Math.floor(Math.random() * 10) + 1 - 5);
                    final Vector3Builder y = localAxes.getY().length(Math.floor(Math.random() * 5 + 1 - 2.5));
                    magicCircle(player, x.copy().add(y), (float) (Math.floor(Math.random() * 4) + 2));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.8f);

                    break;
                }
                case null, default: break;
            }
        });
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();

        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

        final MojangsonByte b = compound.has("locked") ? compound.get("locked", MojangsonValueTypes.BYTE) : MojangsonByte.valueOf((byte) 0);

        if (!b.isBooleanValue()) return;

        final boolean locked = b.getAsBooleanValue();

        if (locked && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        if (!player.getInventory().equals(inventory)) return;

        final ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

        final MojangsonByte b = compound.has("locked")
            ? compound.get("locked", MojangsonValueTypes.BYTE)
            : MojangsonByte.valueOf((byte) 0);

        if (!b.isBooleanValue()) return;

        final boolean locked = b.getAsBooleanValue();

        if (locked && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        final ItemStack mainHandItem = event.getMainHandItem();
        final ItemStack offhandItem = event.getOffHandItem();

        final MojangsonCompound compoundMain = ItemStackCustomDataAccess.of(mainHandItem).read();
        final MojangsonCompound compoundOff = ItemStackCustomDataAccess.of(offhandItem).read();

        final Boolean mainHandLocked = compoundMain.has("locked")
            ? compoundMain.get("locked", MojangsonValueTypes.BYTE).getAsBooleanValueOrNull()
            : null;
        final Boolean offHandLocked = compoundOff.has("locked")
            ? compoundOff.get("locked", MojangsonValueTypes.BYTE).getAsBooleanValueOrNull()
            : null;

        if (Objects.equals(mainHandLocked, true) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            return;
        }

        if (Objects.equals(offHandLocked, true) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack serverSelector = TPLCore.paperVelocityManager.getServerSelectorItemStack();

        final ListIterator<ItemStack> itemStacks = inventory.iterator();

        int i = 0;
        while (itemStacks.hasNext()) {
            final ItemStack itemStack = itemStacks.next();

            if (itemStack == null) continue;

            final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();

            final String tag = compound.has("custom_item_tag")
                ? compound.get("custom_item_tag", MojangsonValueTypes.STRING).getValue()
                : null;

            if (Objects.equals(tag, "server_selector")) return;

            if (i > 100) return;
            i++;
        }

        inventory.addItem(serverSelector);
    }

    private static void magicCircle(Player player, Vector3Builder offset, float size) {
        final Vector3Builder center = Vector3Builder.from(player.getEyeLocation())
        .add(DualAxisRotationBuilder.from(player).getDirection3d().length(2))
        .add(offset);

        final World world = player.getWorld();

        final Shape pointedStar = new Shape(Shape.ShapeType.EIGHT_POINTED_STAR, DualAxisRotationBuilder.from(player));
        pointedStar.setScale(size);
        pointedStar.rotate((float) (Math.random() * 360));
        pointedStar.setParticleDecoration(new Shape.DustDecoration().setCount(3));
        pointedStar.draw(world, center);

        final Shape pentagram = new Shape(Shape.ShapeType.PENTAGRAM, DualAxisRotationBuilder.from(player));
        pentagram.setScale(size / 2);
        pentagram.rotate((float) (Math.random() * 360));
        pentagram.setParticleDecoration(new Shape.DustDecoration().setSize(0.75f).setCount(3));
        pentagram.draw(world, center);

        final Shape circle = new Shape(Shape.ShapeType.PERFECT_CIRCLE, DualAxisRotationBuilder.from(player));
        circle.setScale(size);
        circle.setParticleDecoration(new Shape.DustDecoration().setSize(0.75f));
        circle.draw(world, center);

        final Shape smallCircle = new Shape(Shape.ShapeType.PERFECT_CIRCLE, DualAxisRotationBuilder.from(player));
        smallCircle.setDensity(0.05f);
        smallCircle.setScale(size / 4);
        smallCircle.setParticleDecoration(
            new Shape.DustTransitionDecoration()
            .setFromColor(Color.fromRGB(0xC406FF))
            .setToColor(Color.BLACK)
        );

        for (int i = 0; i < 8; i++) {
            final Vector3Builder vector = circle.getPointOnCircle(center, (float) (Math.random() * 360));
            smallCircle.draw(world, vector);
        }

        final Vector3Builder target = Vector3Builder.from(player.getEyeLocation())
        .add(DualAxisRotationBuilder.from(player).getDirection3d().length(20));

        final DualAxisRotationBuilder direction = center.getDirectionTo(target).getRotation2f();

        final Shape line = new Shape(Shape.ShapeType.STRAIGHT_LINE, direction);
        line.setScale(30);
        line.setParticleDecoration(new Shape.DustDecoration().setColor(Color.fromRGB(0xFF0000)));
        line.draw(world, center);

        final RayTraceResult rayTraceResult = world.rayTraceEntities(center.withWorld(world), direction.getDirection3d().toBukkitVector(), 30);

        if (rayTraceResult != null) {
            final Entity entity = rayTraceResult.getHitEntity();

            if (entity instanceof Damageable) {
                ((Damageable) entity).damage(2, player);
            }
        }

        final Shape.ParticleDecoration flame = new Shape.ParticleDecoration(Particle.FLAME);
        flame.getOffset().add(new Vector3Builder(0.5, 0.5, 0.5));
        flame.setSpeed(0.2);
        line.setParticleDecoration(flame);
        line.draw(world, center);

        world.spawnParticle(
            Particle.PORTAL,
            center.withWorld(world),
            30,
            0.8d, 0.8d, 0.8d,
            1.0d
        );

        world.spawnParticle(
            Particle.FLASH,
            center.withWorld(world),
            3,
            0.0d, 0.0d, 0.0d,
            0.0d
        );
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        final World.Environment from = event.getFrom().getWorld().getEnvironment();
        final World.Environment to = event.getTo().getWorld().getEnvironment();

        if (from.equals(World.Environment.NORMAL) && to.equals(World.Environment.NETHER)) {
            event.setCancelled(true);
            TPLCore.paperVelocityManager.transfer(event.getPlayer(), PaperVelocityManager.BoAServer.GAME);
        }
    }

    @EventHandler
    public void onShot(EntityShootBowEvent event) {
        final Entity entity = event.getEntity();
        final Entity projectile = event.getProjectile();

        if (entity instanceof Player && projectile instanceof Arrow) {
            final Player player = (Player) entity;
            final Arrow arrow = (Arrow) projectile;

            if (!arrow.isCritical()) return;

            shootableCountByLeftClick.put(player, 3);
        }
    }

    static private final Map<Player, Integer> shootableCountByLeftClick = new HashMap<>();
}
