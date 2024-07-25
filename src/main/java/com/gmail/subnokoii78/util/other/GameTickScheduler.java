package com.gmail.subnokoii78.util.other;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GameTickScheduler {
    private final BukkitRunnable callback;

    public GameTickScheduler(Consumer<GameTickScheduler> callback) {
        var that = this;
        this.callback = new BukkitRunnable() {
            @Override
            public void run() {
                callback.accept(that);
            }
        };
    }

    public GameTickScheduler(Runnable callback) {
        this.callback = new BukkitRunnable() {
            @Override
            public void run() {
                callback.run();
            }
        };
    }

    public void runTimeout(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("負の遅延は無効です");
        }

        callback.runTaskLater(TestPlugin.getInstance(), delay);
    }

    public void runTimeout() {
        runTimeout(0);
    }

    public void runInterval(long interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("負の間隔は無効です");
        }
        else if (interval == 0) {
            throw new IllegalArgumentException("間隔0は危険です");
        }

        callback.runTaskTimer(TestPlugin.getInstance(), interval, 0L);
    }

    public void runInterval() {
        runInterval(1);
    }

    public void clear() {
        callback.cancel();
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
}
