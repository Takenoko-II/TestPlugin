package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerComboHandler {
    private static final Map<Player, PlayerComboHandler> playerHandlerPair = new HashMap<>();

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

    public int getCurrentComboCount() {
        return comboCount;
    }

    public int nextCombo() {
        if (isInCoolTime) {
            combo.onComboIsInCT(player);
            return -1;
        }

        comboCount++;
        isInCoolTime = true;

        // リログ時に新しくServerPlayerオブジェクトが作られることを願って()
        new GameTickScheduler(() -> isInCoolTime = false).runTimeout(combo.coolTime);

        final int nextComboCount = comboCount;

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

        return nextComboCount;
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
        if (playerHandlerPair.containsKey(player)) {
            return playerHandlerPair.get(player);
        }
        else {
            final PlayerComboHandler handler = new PlayerComboHandler(player);
            playerHandlerPair.put(player, handler);
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

        public void onComboIsInCT(Player player) {}

        public void onComboComplete(Player player) {}

        public void onComboStop(Player player) {}

        public static final Combo FOR_DEBUG = new Combo(3, 30, 6) {
            @Override
            public void onComboIsInCT(Player player) {
                player.sendMessage(Component.text("onComboIsInCT").color(NamedTextColor.GRAY));
            }

            @Override
            public void onComboComplete(Player player) {
                player.sendMessage(Component.text("onComboComplete").color(NamedTextColor.GREEN));
            }

            @Override
            public void onComboStop(Player player) {
                player.sendMessage(Component.text("onComboStop").color(NamedTextColor.RED));
            }
        };
    }
}
