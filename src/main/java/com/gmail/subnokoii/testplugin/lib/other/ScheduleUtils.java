package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduleUtils {
    public static void runTimeoutByGameTick(Runnable callback, long delay) {
        new BukkitRunnable() {
            public void run() {
                callback.run();
            }
        }
        .runTaskLater(TestPlugin.get(), delay);
    }

    public static void runTimeoutByGameTick(Runnable callback) {
        new BukkitRunnable() {
            public void run() {
                callback.run();
            }
        }
        .runTaskLater(TestPlugin.get(), 0L);
    }

    public static void runTimeout(Runnable callback, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        }, delay);
    }
}
