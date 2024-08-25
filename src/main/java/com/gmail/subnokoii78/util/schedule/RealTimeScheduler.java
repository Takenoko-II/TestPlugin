package com.gmail.subnokoii78.util.schedule;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RealTimeScheduler implements Scheduler {
    private final Runnable callback;

    private final Map<Integer, Timer> tasks = new HashMap<>();

    public RealTimeScheduler(Runnable callback) {
        this.callback = callback;
    }

    public RealTimeScheduler(Consumer<RealTimeScheduler> callback) {
        this.callback = () -> callback.accept(this);
    }

    public RealTimeScheduler(BiConsumer<RealTimeScheduler, Integer> callback) {
        this.callback = () -> callback.accept(this, RealTimeScheduler.id);
    }

    private int issue(BiConsumer<Timer, TimerTask> function) {
        final int taskId = id++;

        final var timer = new Timer();
        final var task = new TimerTask() {
            @Override
            public void run() {
                callback.run();
                tasks.remove(taskId);
            }
        };

        function.accept(timer, task);
        tasks.put(taskId, timer);

        return taskId;
    }

    public int runTimeout(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("負の遅延は無効です");
        }

        return issue((timer, timerTask) -> timer.schedule(timerTask, delay));
    }

    public int runTimeout() {
        return runTimeout(0);
    }

    public int runAt(@NotNull Instant time) {
        if (time.isBefore(Instant.now())) {
            throw new IllegalArgumentException("過去の時刻は無効です");
        }

        return issue((timer, timerTask) -> timer.schedule(timerTask, Date.from(time)));
    }

    public void clear(int id) {
        if (!tasks.containsKey(id)) return;

        final var task = tasks.get(id);
        task.cancel();
        tasks.remove(id);
    }

    public void clear() {
        tasks.forEach((k, v) -> v.cancel());
        tasks.clear();
    }

    private static int id = 0;
}
