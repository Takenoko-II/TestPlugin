package com.gmail.subnokoii78.testplugin.system.combat.combos;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class Combo {
    private final String id;

    private final int maxCombo;

    private final int timeToReset;

    private final int coolTime;

    protected Combo(String id, int maxCombo, int timeToReset, int coolTime) {
        this.id = id;
        this.maxCombo = maxCombo;
        this.timeToReset = timeToReset;
        this.coolTime = coolTime;
    }

    public final String getId() {
        return id;
    }

    public final int getMaxCombo() {
        return maxCombo;
    }

    public final int getTimeToReset() {
        return timeToReset;
    }

    public final int getCoolTime() {
        return coolTime;
    }

    public void onComboProgress(Player player, int currentComboCount) {

    }

    public void onComboIsInCT(Player player) {

    }

    public void onComboComplete(Player player) {

    }

    public void onComboStop(Player player) {

    }
}
