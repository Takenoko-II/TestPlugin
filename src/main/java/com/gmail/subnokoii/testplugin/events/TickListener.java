package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.lib.scoreboard.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TickListener extends BukkitRunnable {
    @Override
    public void run() {
        final ScoreboardUtilsObjective objective = ScoreboardUtils.getObjective("plugin.scheduler.tick_listener");

        final ScoreboardUtilsObjective objX = ScoreboardUtils.getObjective("plugin.api.knockback.x");
        final ScoreboardUtilsObjective objY = ScoreboardUtils.getObjective("plugin.api.knockback.y");
        final ScoreboardUtilsObjective objZ = ScoreboardUtils.getObjective("plugin.api.knockback.z");

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (objective.getScore(player) > 0) {
                objective.setScore(player, 0);

                final Vector velocity = player.getVelocity();

                final double x = ((double) objX.getScore(player)) / 1000d;
                final double y = ((double) objY.getScore(player)) / 1000d;
                final double z = ((double) objZ.getScore(player)) / 1000d;

                objX.setScore(player, 0);
                objY.setScore(player, 0);
                objZ.setScore(player, 0);

                player.setVelocity(new Vector(x, y, z).add(velocity));
            }
        }
    }
}
