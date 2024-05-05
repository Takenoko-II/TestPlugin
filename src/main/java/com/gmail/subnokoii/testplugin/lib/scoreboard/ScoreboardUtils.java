package com.gmail.subnokoii.testplugin.lib.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.stream.Stream;

public class ScoreboardUtils {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();

    private static final Scoreboard mainScoreboard = manager.getMainScoreboard();

    private ScoreboardUtils() {}

    public static ScoreboardUtilsObjective getOrCreateObjective(String name) {
        final Objective targetObjective = mainScoreboard.getObjective(name);

        if (targetObjective == null) {
            final Objective newObjective = mainScoreboard.registerNewObjective(name, Criteria.DUMMY, Component.text(name));
            return new ScoreboardUtilsObjective(newObjective);
        }

        return new ScoreboardUtilsObjective(targetObjective);
    }

    public static ScoreboardUtilsObjective getOrCreateObjective(String name, Criteria criteria) {
        final Objective targetObjective = mainScoreboard.getObjective(name);

        if (targetObjective == null) {
            final Objective newObjective = mainScoreboard.registerNewObjective(name, criteria, Component.text(name));
            return new ScoreboardUtilsObjective(newObjective);
        }

        return new ScoreboardUtilsObjective(targetObjective);
    }

    public static ScoreboardUtilsObjective createObjective(String name) {
        final Objective newObjective = mainScoreboard.registerNewObjective(name, Criteria.DUMMY, Component.text(name));
        return new ScoreboardUtilsObjective(newObjective);
    }

    public static ScoreboardUtilsObjective createObjective(String name, Criteria criteria) {
        final Objective newObjective = mainScoreboard.registerNewObjective(name, criteria, Component.text(name));
        return new ScoreboardUtilsObjective(newObjective);
    }

    public static ScoreboardUtilsObjective[] getAllObjectives() {
        final Stream<ScoreboardUtilsObjective> stream = mainScoreboard.getObjectives().stream()
                .map(ScoreboardUtilsObjective::new);

        return stream.toArray(ScoreboardUtilsObjective[]::new);
    }

    public static void removeObjective(String name) {
        final Objective objective = mainScoreboard.getObjective(name);

        if (objective == null) return;

        objective.unregister();
    }
}
