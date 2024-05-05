package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.vector.RotationBuilder;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.ServerTickManager;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TestPluginEventListener {
    public static void init() {
        TestPlugin.events().onLeftClick(event -> {
            PlayerEventListener.get().onLeftClick(event);

            final ItemStack itemStack = event.getItemStack();

            ScoreboardUtils
            .getOrCreateObjective("plugin.events.player.left_click")
            .addScore(event.getPlayer(), 1);

            final String type = NBTEditor.getString(itemStack, "plugin", "on_left_click", "type");
            final String content = NBTEditor.getString(itemStack, "plugin", "on_left_click", "content");

            if (type != null && content != null) {
                if (type.equals("run_command")) {
                    TestPlugin.runCommand(event.getPlayer(), content);
                }
            }
        });

        TestPlugin.events().onRightClick(event -> {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItemStack();

            if (itemStack == null) return;

            final String type = NBTEditor.getString(itemStack, "plugin", "on_right_click", "type");
            final String content = NBTEditor.getString(itemStack, "plugin", "on_right_click", "content");

            if (type != null && content != null) {
                switch (type) {
                    case "run_command":
                        TestPlugin.runCommand(player, content);
                        break;
                }
            }
        });

        TestPlugin.events().onEnable(() -> {
            // TestPlugin.log("Server", "enable");
        });

        TestPlugin.events().onCustomItemUse(event -> {
            final Player player = event.getPlayer();

            switch (event.getTag()) {
                case "quick_teleporter": {
                    event.cancel();

                    final Block block = player.getTargetBlockExact(127);
                    final BlockFace face = player.getTargetBlockFace(127);
                    final Location previousLocation = player.getLocation();

                    if (block == null || face == null) break;

                    final Vector3Builder location = Vector3Builder.from(block.getLocation());
                    final Vector3Builder direction = RotationBuilder.from(previousLocation).getDirection3d();

                    location.add(new Vector3Builder(face.getModX(), face.getModY(), face.getModZ())).subtract(direction.length(0.5d));

                    final Location destination = location.mergeWithLocation(previousLocation);

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
                        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, entity.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        final NBTEditor.NBTCompound nbt = NBTEditor.getNBTCompound(entity);

                        if (nbt == null) {
                            throw new RuntimeException("NBT持ってないエンティティなんておるわけないやろ！");
                        }

                        player.sendMessage(Component.text(entity.getType().name() + "は以下のNBTを持っています:\n" + nbt.toJson()));

                    }
                    else if (block != null) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        final NBTEditor.NBTCompound nbt = NBTEditor.getNBTCompound(block);

                        if (nbt == null) {
                            player.sendMessage(Component.text("そのブロックはNBTを持っていません").color(TextColor.color(252, 64, 72)));
                            break;
                        }

                        player.sendMessage(Component.text(block.getType().name() + "は以下のNBTを持っています:\n" + nbt.toJson()));
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
                    TestPlugin.openServerSelector(player);
                }
            }
        });
    }
}
