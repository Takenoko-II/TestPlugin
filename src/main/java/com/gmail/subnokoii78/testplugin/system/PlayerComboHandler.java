package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PlayerComboHandler {
    private static final Map<Player, PlayerComboHandler> comboHandlerMap = new HashMap<>();

    private PlayerComboHandler(Player player) {
        this.player = player;
    }

    private final Player player;

    private int comboCount = 0;

    private Combo combo = Combo.FOR_DEBUG;

    private boolean isAwaitingNextCombo = false;

    private boolean isInCoolTime = false;

    private final GameTickScheduler scheduler = new GameTickScheduler(scheduler -> {
        if (isAwaitingNextCombo) {
            isAwaitingNextCombo = false;
            stopCombo();
        }
    });

    public @Nullable Integer nextCombo() {
        if (isInCoolTime) {
            return null;
        }

        comboCount++;
        isInCoolTime = true;

        // リログ時に新しくServerPlayerオブジェクトが作られることを願って()
        new GameTickScheduler(() -> isInCoolTime = false).runTimeout(combo.coolTime);

        final int nextCount = comboCount;

        if (comboCount >= combo.maxCombo) {
            // コンボ完成
            combo.onComboComplete(player);
            comboCount = 0;
        }
        else {
            // コンボ未完成
            scheduler.clear();
            scheduler.runTimeout(combo.timeToReset);
            isAwaitingNextCombo = true;
        }
        return nextCount;
    }

    public void stopCombo() {
        if (comboCount > 0) {
            comboCount = 0;
            combo.onComboStop(player);
        }
    }

    public Combo combo() {
        return combo;
    }

    public void combo(Combo combo) {
        Objects.requireNonNull(combo, "combo object must be not null");
        this.combo = combo;
    }

    public static PlayerComboHandler getHandler(Player player) {
        if (comboHandlerMap.containsKey(player)) {
            return comboHandlerMap.get(player);
        }
        else {
            final PlayerComboHandler handler = new PlayerComboHandler(player);
            comboHandlerMap.put(player, handler);
            return handler;
        }
    }

    public static abstract class Combo {
        private final int maxCombo;

        private final int timeToReset;

        private final int coolTime;

        protected Combo(int maxCombo, int timeToReset, int coolTime) {
            this.maxCombo = maxCombo;
            this.timeToReset = timeToReset;
            this.coolTime = coolTime;
        }

        public int getMaxCombo() {
            return maxCombo;
        }

        public int getTimeToReset() {
            return timeToReset;
        }

        public int getCoolTime() {
            return coolTime;
        }

        public abstract void onComboComplete(Player player);

        public abstract void onComboStop(Player player);

        public static final Combo FOR_DEBUG = new Combo(3, 30, 6) {
            @Override
            public void onComboComplete(Player player) {
                player.sendMessage(Component.text("onBeforeComboComplete").color(NamedTextColor.GREEN));
            }

            @Override
            public void onComboStop(Player player) {
                player.sendMessage(Component.text("onComboStop").color(NamedTextColor.RED));
            }
        };
    }
}
