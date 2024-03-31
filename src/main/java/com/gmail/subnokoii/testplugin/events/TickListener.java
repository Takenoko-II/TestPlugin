package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtilsObjective;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TickListener extends BukkitRunnable {
    @Override
    public void run() {
        final ScoreboardUtilsObjective objective = ScoreboardUtils.getObjective("plugin.scheduler.tick_listener");

        final ScoreboardUtilsObjective objx = ScoreboardUtils.getObjective("plugin.api.knockback.x");
        final ScoreboardUtilsObjective objy = ScoreboardUtils.getObjective("plugin.api.knockback.y");
        final ScoreboardUtilsObjective objz = ScoreboardUtils.getObjective("plugin.api.knockback.z");

        for (Player player: Bukkit.getServer().getOnlinePlayers()) {
            if (objective.getScore(player) > 0) {
                objective.setScore(player, 0);

                final Vector velocity = player.getVelocity();

                final double x = ((double) objx.getScore(player)) / 1000d;
                final double y = ((double) objy.getScore(player)) / 1000d;
                final double z = ((double) objz.getScore(player)) / 1000d;

                objx.setScore(player, 0);
                objy.setScore(player, 0);
                objz.setScore(player, 0);

                player.setVelocity(new Vector(x, y, z).add(velocity));
            }
        }
    }
}
