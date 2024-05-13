package com.gmail.subnokoii.testplugin.lib.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.Nullable;

public class ScoreboardUtils {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();

    private static final Scoreboard mainScoreboard = manager.getMainScoreboard();

    public static Objective createObjective(String name) {
        final org.bukkit.scoreboard.Objective newObjective = mainScoreboard
        .registerNewObjective(name, Criteria.DUMMY, Component.text(name));

        return new Objective(newObjective);
    }

    public static Objective createObjective(String name, Criteria criteria) {
        final org.bukkit.scoreboard.Objective newObjective = mainScoreboard
        .registerNewObjective(name, criteria, Component.text(name));

        return new Objective(newObjective);
    }

    public static @Nullable Objective getObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = mainScoreboard.getObjective(name);

        if (objective == null) return null;

        return new Objective(objective);
    }

    public static Objective getOrCreateObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = mainScoreboard.getObjective(name);

        if (objective == null) return createObjective(name);

        return new Objective(objective);
    }

    public static Objective getOrCreateObjective(String name, Criteria criteria) {
        final org.bukkit.scoreboard.Objective objective = mainScoreboard.getObjective(name);

        if (objective == null) return createObjective(name, criteria);

        return new Objective(objective);
    }

    public static Objective[] getAllObjectives() {
        return mainScoreboard
        .getObjectives()
        .stream()
        .map(Objective::new)
        .toArray(Objective[]::new);
    }

    public static void removeObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = mainScoreboard.getObjective(name);

        if (objective == null) return;

        objective.unregister();
    }

    public static final class Objective {
        private final org.bukkit.scoreboard.Objective objective;

        private Objective(org.bukkit.scoreboard.Objective objective) {
            this.objective = objective;
        }

        public int getScore(Entity entity) {
            return objective.getScoreFor(entity).getScore();
        }

        public int getScore(String name) {
            return objective.getScore(name).getScore();
        }

        public void setScore(Entity entity, int value) {
            objective.getScoreFor(entity).setScore(value);
        }

        public void setScore(String name, int value) {
            objective.getScore(name).setScore(value);
        }

        public void addScore(Entity entity, int value) {
            final int addend = getScore(entity) + value;
            setScore(entity, addend);
        }

        public void addScore(String name, int value) {
            final int addend = getScore(name) + value;
            setScore(name, addend);
        }

        public void resetScore(Entity entity) {
            objective.getScoreFor(entity).resetScore();
        }

        public void resetScore(String name) {
            objective.getScore(name).resetScore();
        }

        public String getName() {
            return objective.getName();
        }

        public Component getDisplayName() {
            return objective.displayName();
        }

        public void setDisplayName(Component displayName) {
            objective.displayName(displayName);
        }

        public DisplaySlot getDisplaySlot() {
            return objective.getDisplaySlot();
        }

        public void setDisplaySlot(DisplaySlot displaySlot) {
            objective.setDisplaySlot(displaySlot);
        }
    }
}
