package com.gmail.subnokoii78.util.schedule;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * tick単位で遅延をかけて処理を実行するためのクラス
 */
public final class GameTickScheduler implements Scheduler {
    private final Runnable callback;

    private final Map<Integer, BukkitTask> tasks = new HashMap<>();

    public GameTickScheduler(Runnable callback) {
        this.callback = callback;
    }

    public GameTickScheduler(Consumer<GameTickScheduler> callback) {
        this.callback = () -> callback.accept(this);
    }

    public GameTickScheduler(BiConsumer<GameTickScheduler, Integer> callback) {
        this.callback = () -> callback.accept(this, GameTickScheduler.id);
    }

    private int issue(Function<BukkitRunnable, BukkitTask> function) {
        final int taskId = id++;
        final var runnable = new BukkitRunnable() {
            @Override
            public void run() {
                callback.run();
                tasks.remove(taskId);
            }
        };

        tasks.put(taskId, function.apply(runnable));

        return taskId;
    }

    public int runTimeout(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("負の遅延は無効です");
        }

        return issue(runnable -> runnable.runTaskLater(TestPlugin.getInstance(), delay));
    }

    public int runTimeout() {
        return runTimeout(0);
    }

    public int runAt(@NotNull World world, long gameTime) {
        if (gameTime < world.getGameTime()) {
            throw new IllegalArgumentException("過去の時刻は無効です");
        }

        return runTimeout(gameTime - world.getGameTime());
    }

    public int runInterval(long interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("負の間隔は無効です");
        }
        else if (interval == 0) {
            throw new IllegalArgumentException("間隔0は危険です");
        }

        return issue(runnable -> runnable.runTaskTimer(TestPlugin.getInstance(), interval, 0L));
    }

    public int runInterval() {
        return runInterval(1);
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

    /**
     * 指定時間(ミリ秒)後に関数を実行します。
     * @param callback 実行する処理
     * @param delay 遅延する時間(ミリ秒)
     * @deprecated tickの処理に割り込むことができるため、予期しないエラーが発生する可能性があります。
     */
    public static void runTimeout(Runnable callback, long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("負の遅延は無効です");
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        }, delay);
    }
}
