package com.gmail.subnokoii78.testplugin.system.field;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import net.kyori.adventure.text.Component;

public class AutoFlusher {
    private final GameFieldRestorer restorer;

    private final GameTickScheduler scheduler;

    public static final int AUTO_FLUSH_BATCH_COUNT = 30;

    public static final int AUTO_FLUSH_INTERVAL = 10;

    private boolean isEnabled = false;

    private boolean isDisabled = false;

    AutoFlusher(GameFieldRestorer restorer) {
        this.restorer = restorer;
        this.scheduler = new GameTickScheduler(s -> {
            if (isDisabled) return;

            final int count = restorer.flush(AUTO_FLUSH_BATCH_COUNT);

            TPLCore.getPlugin().getComponentLogger().info(Component.text(
                count + "件のバッチのオートフラッシュを実行しました"
            ));

            if (restorer.getBatchCount() > 0) {
                s.runTimeout(AUTO_FLUSH_INTERVAL);
            }
            else {
                isEnabled = false;
            }
        });
    }

    public void update() {
        if (isEnabled) {
            return;
        }

        if (restorer.getBatchCount() >= AUTO_FLUSH_BATCH_COUNT) {
            isEnabled = true;
            scheduler.runTimeout(AUTO_FLUSH_INTERVAL);
        }
    }

    public void disable() {
        scheduler.clear();
        isEnabled = false;
        isDisabled = true;
    }
}
