package com.gmail.subnokoii.testplugin.lib.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.function.UnaryOperator;

public class ScoreboardUtilsObjective {
    private final Objective objective;

    public ScoreboardUtilsObjective(Objective objective) {
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
