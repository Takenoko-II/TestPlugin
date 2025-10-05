package com.gmail.subnokoii78.testplugin.system.combat;

import com.gmail.subnokoii78.testplugin.system.combat.combos.Combo;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class PlayerComboHandle {
    private static final Map<Player, PlayerComboHandle> playerHandlerPair = new HashMap<>();

    private PlayerComboHandle(Player player) {
        this.player = player;
    }

    private final Player player;

    private int comboCount = 0;

    @Nullable
    private Combo combo = null;

    private boolean isAwaitingNextCombo = false;

    private boolean isInCoolTime = false;

    private long lastComboTick = 0L;

    private final GameTickScheduler scheduler = new GameTickScheduler(scheduler -> {
        if (isAwaitingNextCombo) {
            isAwaitingNextCombo = false;
            stopCombo();
        }
    });

    public boolean nextCombo() {
        return nextCombo(null);
    }

    public boolean nextCombo(@Nullable Object data) {
        if (isInCoolTime) {
            getCombo().onComboIsInCT(player);
            return false;
        }

        comboCount++;
        isInCoolTime = true;

        // リログ時に新しくServerPlayerオブジェクトが作られることを願って()
        new GameTickScheduler(() -> isInCoolTime = false).runTimeout(getCombo().getCoolTime());

        final int nextComboCount = comboCount;

        boolean comboCompleted = false;
        if (comboCount >= getCombo().getMaxCombo()) {
            // コンボ完成
            comboCompleted = true;
            comboCount = 0;
        }
        else {
            // コンボ未完成
            scheduler.clear();
            scheduler.runTimeout(getCombo().getTimeToReset());
            isAwaitingNextCombo = true;
        }

        getCombo().onComboProgress(player, nextComboCount, data);

        if (comboCompleted) {
            getCombo().onComboComplete(player, data);
        }

        lastComboTick = Bukkit.getServer().getCurrentTick();

        return nextComboCount > 0;
    }

    public long getLastComboTick() {
        return lastComboTick;
    }

    public void stopCombo() {
        if (comboCount > 0) {
            comboCount = 0;
            getCombo().onComboStop(player);
        }
    }

    public Combo getCombo() {
        if (combo == null) {
            throw new IllegalStateException("プレイヤー '" + player.getName() + "' は職業を持っていません");
        }

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
