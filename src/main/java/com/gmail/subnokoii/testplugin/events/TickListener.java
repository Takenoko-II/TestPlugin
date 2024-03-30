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

        for (Player player: Bukkit.getServer().getOnlinePlayers()) {
            if (objective.getScore(player) > 0) {
                objective.setScore(player, 0);

                final double x = ((double) objective.getScore("$x")) / 1000d;
                final double y = ((double) objective.getScore("$y")) / 1000d;
                final double z = ((double) objective.getScore("$z")) / 1000d;

                player.setVelocity(new Vector(x, y, z));
            }
        }
    }
}
