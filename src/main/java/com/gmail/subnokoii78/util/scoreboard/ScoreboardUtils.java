package com.gmail.subnokoii78.util.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.Nullable;

public class ScoreboardUtils {
    private static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    public static Objective createObjective(String name) {
        final org.bukkit.scoreboard.Objective newObjective = scoreboard.registerNewObjective(name, Criteria.DUMMY, Component.text(name));

        return new Objective(newObjective);
    }

    public static Objective createObjective(String name, Criteria criteria) {
        final org.bukkit.scoreboard.Objective newObjective = scoreboard.registerNewObjective(name, criteria, Component.text(name));

        return new Objective(newObjective);
    }

    public static @Nullable Objective getObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);

        if (objective == null) return null;

        return new Objective(objective);
    }

    public static Objective getOrCreateObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);

        if (objective == null) return createObjective(name);

        return new Objective(objective);
    }

    public static Objective getOrCreateObjective(String name, Criteria criteria) {
        final org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);

        if (objective == null) return createObjective(name, criteria);

        return new Objective(objective);
    }

    public static Objective[] getAllObjectives() {
        return scoreboard
        .getObjectives()
        .stream()
        .map(Objective::new)
        .toArray(Objective[]::new);
    }

    public static void removeObjective(String name) {
        final org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);

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

        public Objective setScore(Entity entity, int value) {
            objective.getScoreFor(entity).setScore(value);

            return this;
        }

        public Objective setScore(String name, int value) {
            objective.getScore(name).setScore(value);

            return this;
        }

        public Objective addScore(Entity entity, int value) {
            setScore(entity, getScore(entity) + value);

            return this;
        }

        public Objective addScore(String name, int value) {
            setScore(name, getScore(name) + value);

            return this;
        }

        public Objective subtractScore(Entity entity, int value) {
            setScore(entity, getScore(entity) - value);

            return this;
        }

        public Objective subtractScore(String name, int value) {
            setScore(name, getScore(name) - value);

            return this;
        }

        public Objective multiplyScore(Entity entity, int value) {
            setScore(entity, getScore(entity) * value);

            return this;
        }

        public Objective multiplyScore(String name, int value) {
            final int subtrahend = getScore(name) * value;
            setScore(name, subtrahend);

            return this;
        }

        public Objective divideScore(Entity entity, int value) {
            if (value == 0) return this;

            setScore(entity, getScore(entity) / value);

            return this;
        }

        public Objective divideScore(String name, int value) {
            if (value == 0) return this;

            setScore(name, getScore(name) / value);

            return this;
        }

        public Objective resetScore(Entity entity) {
            objective.getScoreFor(entity).resetScore();

            return this;
        }

        public Objective resetScore(String name) {
            objective.getScore(name).resetScore();

            return this;
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
