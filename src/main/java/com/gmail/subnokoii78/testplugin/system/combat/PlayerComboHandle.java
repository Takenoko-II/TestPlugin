package com.gmail.subnokoii78.testplugin.system.combat;

import com.gmail.subnokoii78.testplugin.system.combat.combos.Combo;
import com.gmail.subnokoii78.testplugin.system.combat.combos.KnightSlash;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class PlayerComboHandle {
    private static final Map<Player, PlayerComboHandle> playerHandlerPair = new HashMap<>();

    private PlayerComboHandle(Player player) {
        this.player = player;
    }

    private final Player player;

    private int comboCount = 0;

    private Combo combo = KnightSlash.KNIGHT_SLASH;

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

        boolean comboCompleted = false;
        if (comboCount >= combo.getMaxCombo()) {
            // コンボ完成
            comboCompleted = true;
            comboCount = 0;
        }
        else {
            // コンボ未完成
            scheduler.clear();
            scheduler.runTimeout(combo.getTimeToReset());
            isAwaitingNextCombo = true;
        }

        combo.onComboProgress(player, nextComboCount);

        if (comboCompleted) {
            combo.onComboComplete(player);
        }

        return nextComboCount > 0;
    }

    public void stopCombo() {
        if (comboCount > 0) {
            comboCount = 0;
            combo.onComboStop(player);
        }
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        Objects.requireNonNull(combo, "Combo object must be not null");
        this.combo = combo;
    }

    public static PlayerComboHandle getHandle(Player player) {
        if (playerHandlerPair.containsKey(player)) {
            return playerHandlerPair.get(player);
        }
        else {
            final PlayerComboHandle handler = new PlayerComboHandle(player);
            playerHandlerPair.put(player, handler);
            return handler;
        }
    }
}
