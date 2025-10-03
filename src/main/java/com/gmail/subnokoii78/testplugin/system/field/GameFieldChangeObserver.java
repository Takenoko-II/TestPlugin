package com.gmail.subnokoii78.testplugin.system.field;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class GameFieldChangeObserver implements Listener {
    public boolean isEnabled = false;

    private GameFieldChangeObserver() {}

    private boolean isValid(World world) {
        return isEnabled && world.equals(TestPlugin.getGameFieldRestorer().getWorld());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.canBuild()) return;

        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlockPlaced().getLocation()).toIntVector(true),
            event.getBlockReplacedState().getBlockData()
        );
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock().getLocation()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        for (final Block block : event.blockList()) {
            TestPlugin.getGameFieldRestorer().batch(
                Vector3Builder.from(block).toIntVector(true),
                block.getBlockData()
            );
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!isValid(event.getLocation().getWorld())) {
            return;
        }

        for (final Block block : event.blockList()) {
            TestPlugin.getGameFieldRestorer().batch(
                Vector3Builder.from(block).toIntVector(true),
                block.getBlockData()
            );
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock().getLocation()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock().getLocation()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock().getLocation()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getToBlock()).toIntVector(true),
            event.getToBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        if (!isValid(event.getBlock().getWorld())) {
            return;
        }

        TestPlugin.getGameFieldRestorer().batch(
            Vector3Builder.from(event.getBlock()).toIntVector(true),
            event.getBlock().getBlockData()
        );
    }

    public static final GameFieldChangeObserver INSTANCE = new GameFieldChangeObserver();
}
