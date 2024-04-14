package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.lib.ui.ChestUIBuilder;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class UICommand implements CommandExecutor {
    private final Consumer<ChestUIClickEvent> giveClickedItem = response -> {
        response.getPlayer().getInventory().addItem(response.getClicked().getItemStack());
    };

    private final ChestUIBuilder main = new ChestUIBuilder("Tools", 1)
    .set(0, mainBuilder -> {
        return mainBuilder.type(Material.APPLE)
        .name("Items")
        .lore("アイテムの入手", Color.GRAY)
        .onClick(mainResponse -> {
            new ChestUIBuilder("Items", 1)
            .set(0, builder -> {
                return builder.type(Material.COMMAND_BLOCK)
                .onClick(giveClickedItem);
            })
            .set(1, builder -> {
                return builder.type(Material.REPEATING_COMMAND_BLOCK)
                .onClick(giveClickedItem);
            })
            .set(2, builder -> {
                return builder.type(Material.CHAIN_COMMAND_BLOCK)
                .onClick(giveClickedItem);
            })
            .set(3, builder -> {
                return builder.type(Material.BARRIER)
                .onClick(giveClickedItem);
            })
            .set(4, builder -> {
                return builder.type(Material.STRUCTURE_BLOCK)
                .onClick(giveClickedItem);
            })
            .set(5, builder -> {
                return builder.type(Material.STRUCTURE_VOID)
                .onClick(giveClickedItem);
            })
            .set(6, builder -> {
                return builder.type(Material.LIGHT)
                .onClick(giveClickedItem);
            })
            .set(7, builder -> {
                return builder.type(Material.DEBUG_STICK)
                .onClick(giveClickedItem);
            })
            .open(mainResponse.getPlayer());
        });
    })
    .set(1, mainBuilder -> {
        return mainBuilder.type(Material.ENDER_PEARL)
        .name("Teleport")
        .lore("テレポート", Color.GRAY)
        .onClick(mainResponse -> {
            new ChestUIBuilder("Teleport", 1)
            .set(0, builder -> {
                return builder.type(Material.RED_BED)
                .name("SpawnPoint")
                .lore("ワールドのスポーン地点にテレポートする", Color.GRAY)
                .onClick(response -> {
                    final Location location = response.getPlayer().getWorld().getSpawnLocation();
                    response.getPlayer().teleport(location);
                });
            })
            .set(1, builder -> {
                return builder.type(Material.GRASS_BLOCK)
                .name("OverWorld")
                .lore("オーバーワールドにテレポートする", Color.GRAY)
                .onClick(response -> {
                    final Location location = response.getPlayer().getLocation();
                    location.setWorld(Bukkit.createWorld(new WorldCreator("world")));

                    response.getPlayer().teleport(location);
                });
            })
            .set(2, builder -> {
                return builder.type(Material.NETHERRACK)
                .name("Nether")
                .lore("ネザーにテレポートする",Color.GRAY)
                .onClick(response -> {
                    final Location location = response.getPlayer().getLocation();
                    location.setWorld(Bukkit.createWorld(new WorldCreator("world_nether")));

                    response.getPlayer().teleport(location);
                });
            })
            .set(3, builder -> {
                return builder.type(Material.END_STONE)
                .name("The End")
                .lore("ジ・エンドにテレポートする", Color.GRAY)
                .onClick(response -> {
                    final Location location = response.getPlayer().getLocation();
                    location.setWorld(Bukkit.createWorld(new WorldCreator("world_the_end")));

                    response.getPlayer().teleport(location);
                });
            })
            .set(4, builder -> {
                return builder.type(Material.PLAYER_HEAD)
                .name("Player")
                .lore("プレイヤーにテレポートする", Color.GRAY)
                .onClick(response -> {
                    final ChestUIBuilder playersUI = new ChestUIBuilder("Teleport to Player", 1);
                    final Player[] players = response.getPlayer().getWorld().getPlayers().toArray(new Player[0]);

                    for (int i = 0; i < Math.min(9, players.length); i++) {
                        final Player targetPlayer = players[i];
                        playersUI.set(i, headBuilder -> {
                            return headBuilder.type(Material.PLAYER_HEAD)
                            .name(targetPlayer.getName())
                            .playerHead(targetPlayer)
                            .onClick(headResponse -> {
                                headResponse.getPlayer().teleport(targetPlayer);
                            });
                        });
                    }

                    playersUI.open(response.getPlayer());
                });
            })
            .open(mainResponse.getPlayer());
        });
    })
    .set(2, mainBuilder -> {
        return mainBuilder.type(Material.POTION)
        .name("Effects")
        .lore("エフェクト効果を付与する", Color.GRAY)
        .potion(PotionType.WATER)
        .onClick(mainResponse -> {
            final Player player = mainResponse.getPlayer();

            new ChestUIBuilder("Effects", 1)
            .set(0, builder -> {
                return builder.type(Material.POTION)
                .name("Night Vision")
                .potion(PotionType.NIGHT_VISION)
                .onClick(response -> {
                    final PotionEffectType nightVision = PotionEffectType.NIGHT_VISION;

                    if (player.hasPotionEffect(nightVision)) {
                        player.removePotionEffect(nightVision);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(nightVision, 2147483647, 0, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .set(1, builder -> {
                return builder.type(Material.POTION)
                .name("Saturation")
                .potion(PotionType.UNCRAFTABLE)
                .onClick(response -> {
                    final PotionEffectType saturation = PotionEffectType.SATURATION;

                    if (player.hasPotionEffect(saturation)) {
                        player.removePotionEffect(saturation);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(saturation, 2147483647, 4, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .set(2, builder -> {
                return builder.type(Material.POTION)
                .name("Instant Health")
                .potion(PotionType.INSTANT_HEAL)
                .onClick(response -> {
                    final PotionEffectType heal = PotionEffectType.HEAL;

                    if (player.hasPotionEffect(heal)) {
                        player.removePotionEffect(heal);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(heal, 2147483647, 28, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .set(3, builder -> {
                return builder.type(Material.POTION)
                .name("Resistance")
                .potion(PotionType.UNCRAFTABLE)
                .onClick(response -> {
                    final PotionEffectType resistance = PotionEffectType.DAMAGE_RESISTANCE;

                    if (player.hasPotionEffect(resistance)) {
                        player.removePotionEffect(resistance);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(resistance, 2147483647, 4, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .set(4, builder -> {
                return builder.type(Material.POTION)
                .name("Water Breathing")
                .potion(PotionType.WATER_BREATHING)
                .onClick(response -> {
                    final PotionEffectType breathing = PotionEffectType.WATER_BREATHING;

                    if (player.hasPotionEffect(breathing)) {
                        player.removePotionEffect(breathing);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(breathing, 2147483647, 0, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .set(5, builder -> {
                return builder.type(Material.POTION)
                .name("Fire Resistance")
                .potion(PotionType.FIRE_RESISTANCE)
                .onClick(response -> {
                    final PotionEffectType fireResistance = PotionEffectType.FIRE_RESISTANCE;

                    if (player.hasPotionEffect(fireResistance)) {
                        player.removePotionEffect(fireResistance);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10f, 2f);
                    }
                    else {
                        final PotionEffect effect = new PotionEffect(fireResistance, 2147483647, 0, true, false);
                        player.addPotionEffect(effect);
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10f, 2f);
                    }
                });
            })
            .open(mainResponse.getPlayer());
        });
    })
    .set(3, mainBuilder -> {
        return mainBuilder.type(Material.CLOCK)
        .name("Ticks")
        .lore("ティックの管理", Color.GRAY)
        .onClick(mainResponse -> {
            final Player player = mainResponse.getPlayer();

            final ServerTickManager manager = player.getServer().getServerTickManager();

            new ChestUIBuilder("Ticks", 1)
            .set(0, builder -> {
                return builder.type(Material.ICE)
                .name("Freeze/Unfreeze")
                .glint(manager.isFrozen())
                .onClick(response -> {
                    if (manager.isFrozen()) {
                        manager.setFrozen(false);
                        response.playSound(Sound.ENTITY_WITHER_SPAWN, 10f, 2f);
                        builder.glint(false);
                    }
                    else {
                        manager.setFrozen(true);
                        response.playSound(Sound.BLOCK_ANVIL_USE, 10f, 2f);
                        builder.glint(true);
                    }
                });
            })
            .open(player);
        });
    });

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (!sender.isOp()) {
            sender.sendMessage("OP権限ないとつかえないよ!");
             return false;
        }

        final Player player = (Player) sender;
        main.open(player);

        return true;
    }
}
