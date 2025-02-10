package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerComboHandler {
    private static final Map<Player, PlayerComboHandler> playerHandlerPair = new HashMap<>();

    private PlayerComboHandler(@NotNull Player player) {
        this.player = player;
    }

    private final Player player;

    private int comboCount = 0;

    private Combo combo = Combo.KNIGHT_NORMAL_SLASH;

    private boolean isAwaitingNextCombo = false;

    private boolean isInCoolTime = false;

    private final GameTickScheduler scheduler = new GameTickScheduler(scheduler -> {
        if (isAwaitingNextCombo) {
            isAwaitingNextCombo = false;
            stopCombo();
        }
    });

    public boolean nextCombo() {
        if (isInCoolTime) {
            combo.onComboIsInCT(player);
            return false;
        }

        comboCount++;
        isInCoolTime = true;

        // リログ時に新しくServerPlayerオブジェクトが作られることを願って()
        new GameTickScheduler(() -> isInCoolTime = false).runTimeout(combo.getCoolTime());

        final int nextComboCount = comboCount;

        if (comboCount >= combo.getMaxCombo()) {
            // コンボ完成
            combo.onComboComplete(player);
            comboCount = 0;
        }
        else {
            // コンボ未完成
            scheduler.clear();
            scheduler.runTimeout(combo.getTimeToReset());
            isAwaitingNextCombo = true;
        }

        combo.onComboProgress(player, nextComboCount);

        return nextComboCount > 0;
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

    public void combo(@NotNull Combo combo) {
        Objects.requireNonNull(combo, "Combo object must be not null");
        this.combo = combo;
    }

    public static PlayerComboHandler getHandler(@NotNull Player player) {
        if (playerHandlerPair.containsKey(player)) {
            return playerHandlerPair.get(player);
        }
        else {
            final PlayerComboHandler handler = new PlayerComboHandler(player);
            playerHandlerPair.put(player, handler);
            return handler;
        }
    }
}
