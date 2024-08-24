package com.gmail.subnokoii78.util.other;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameTickScheduler {
    private final Runnable callback;

    private final Map<Integer, BukkitTask> tasks = new HashMap<>();

    public GameTickScheduler(Consumer<GameTickScheduler> callback) {
        this.callback = () -> callback.accept(this);
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

    public GameTickScheduler(Runnable callback) {
        this.callback = callback;
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

    private static int id = 0;
}
