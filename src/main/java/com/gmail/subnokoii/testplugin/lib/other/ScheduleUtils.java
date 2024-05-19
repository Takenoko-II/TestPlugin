package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduleUtils {
    /**
     * 指定時間(tick)後に関数を実行します。
     * @param callback 実行する処理
     * @param delay 遅延する時間(tick)
     */
    public static void runTimeoutByGameTick(Runnable callback, long delay) {
        new BukkitRunnable() {
            public void run() {
                callback.run();
            }
        }
        .runTaskLater(TestPlugin.get(), delay);
    }

    /**
     * 0tick後に関数を実行します。
     * @param callback 実行する処理
     */
    public static void runTimeoutByGameTick(Runnable callback) {
        new BukkitRunnable() {
            public void run() {
                callback.run();
            }
        }
        .runTaskLater(TestPlugin.get(), 0L);
    }

    /**
     * 指定時間(ミリ秒)後に関数を実行します。
     * @param callback 実行する処理
     * @param delay 遅延する時間(ミリ秒)
     * @deprecated tickの処理に割り込むことができるため、予期しないエラーが発生する可能性があります。
     */
    public static void runTimeout(Runnable callback, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        }, delay);
    }
}
