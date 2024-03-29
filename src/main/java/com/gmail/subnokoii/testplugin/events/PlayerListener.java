package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.vector.VectorDimensionSizeMismatchException;
import com.gmail.subnokoii.testplugin.lib.vector.VectorUnexpectedDimensionSizeException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        PlayerListener.isLeftClick.put(event.getPlayer(), false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerListener.lastBlockBreakTimestamp.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws VectorDimensionSizeMismatchException, VectorUnexpectedDimensionSizeException {
        if (event.getAction().isLeftClick()) {
            long lastBlockBreakTimestamp = PlayerListener.lastBlockBreakTimestamp.get(event.getPlayer());

            if (System.currentTimeMillis() - lastBlockBreakTimestamp < 50) {
                return;
            }

            final Player player = event.getPlayer();

            final boolean isNonDrop = PlayerListener.isLeftClick.get(player);
            PlayerListener.isLeftClick.put(player, true);

            if (isNonDrop) {
                PlayerListener.onLeftClick(player);
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) throws VectorDimensionSizeMismatchException, VectorUnexpectedDimensionSizeException {
        if (event.getDamager() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            PlayerListener.onLeftClick((Player) event.getDamager());
        }
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        PlayerListener.isLeftClick.put(event.getPlayer(), true);
        PlayerListener.lastBlockBreakTimestamp.put(event.getPlayer(), 0L);
    }

    private static final Map<Player, Boolean> isLeftClick = new HashMap<Player, Boolean>();

    private static final Map<Player, Long> lastBlockBreakTimestamp = new HashMap<Player, Long>();

    private static void onLeftClick(Player player) throws VectorUnexpectedDimensionSizeException, VectorDimensionSizeMismatchException {

        ScoreboardUtils
                .getObjective("plugin.events.player.left_click")
                .addScore(player, 1);
    }
}
