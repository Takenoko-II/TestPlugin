package com.gmail.subnokoii78.testplugin.system;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Job {
    private static int nextId = 1;

    private final int id;

    protected Job() {
        this.id = nextId++;
    }

    public final int getId() {
        return id;
    }

    public abstract void attack(@NotNull Player attacker, @NotNull Combo combo);

    public static final Job KNIGHT = new Job() {
        @Override
        public void attack(@NotNull Player attacker, @NotNull Combo combo) {

        }
    };
}
