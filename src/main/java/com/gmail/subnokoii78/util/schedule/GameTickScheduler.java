package com.gmail.subnokoii78.util.schedule;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class GameTickScheduler extends AbstractGameTickScheduler {
    public GameTickScheduler(Runnable callback) {
        super(callback);
    }

    public GameTickScheduler(Consumer<AbstractGameTickScheduler> callback) {
        super(callback);
    }

    public GameTickScheduler(BiConsumer<AbstractGameTickScheduler, Integer> callback) {
        super(callback);
    }

    @Override
    protected @NotNull Plugin getPlugin() {
        return TestPlugin.getInstance();
    }
}
