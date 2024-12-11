package com.gmail.subnokoii78.testplugin.system;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;

public abstract class Combo {
    private final Type type;

    private final int maxCombo;

    private final int timeToReset;

    private final int coolTime;

    protected Combo(@NotNull Type type, int maxCombo, int timeToReset, int coolTime) {
        this.type = type;
        this.maxCombo = maxCombo;
        this.timeToReset = timeToReset;
        this.coolTime = coolTime;
    }

    public final @NotNull Type getType() {
        return type;
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

    public abstract @NotNull ItemDisplayAnimator getAnimator(int currentComboCount);

    public void onComboIsInCT(@NotNull Player player) {

    }

    public void onComboComplete(@NotNull Player player) {

    }

    public void onComboStop(@NotNull Player player) {

    }

    public enum Type {
        NORMAL,
        SPECIAL
    }

    public static final Combo KNIGHT_SLASH = new Combo(Type.NORMAL, 4, 30, 6) {
        @Override
        public void onComboIsInCT(@NotNull Player player) {
            player.sendMessage(Component.text("onComboIsInCT").color(NamedTextColor.GRAY));
        }

        @Override
        public void onComboComplete(@NotNull Player player) {
            player.sendMessage(Component.text("onComboComplete").color(NamedTextColor.GREEN));
        }

        @Override
        public void onComboStop(@NotNull Player player) {
            player.sendMessage(Component.text("onComboStop").color(NamedTextColor.RED));
        }

        private final List<ItemDisplayAnimator> animators = List.of(
            new ItemDisplayAnimator(
                2,
                List.of(
                    "knight_slash/normal_combo1/frame1",
                    "knight_slash/normal_combo1/frame2",
                    "knight_slash/normal_combo1/frame3",
                    "knight_slash/normal_combo1/frame4",
                    "knight_slash/normal_combo1/frame5"
                )
            ).displayScale(1, 1, 1),
            new ItemDisplayAnimator(
                2,
                List.of(
                    "knight_slash/normal_combo2/frame1",
                    "knight_slash/normal_combo2/frame2",
                    "knight_slash/normal_combo2/frame3",
                    "knight_slash/normal_combo2/frame4",
                    "knight_slash/normal_combo2/frame5"
                )
            ).displayScale(1, 1, 1),
            new ItemDisplayAnimator(
                2,
                List.of(
                    "knight_slash/normal_combo3/frame1",
                    "knight_slash/normal_combo3/frame2",
                    "knight_slash/normal_combo3/frame3",
                    "knight_slash/normal_combo3/frame4",
                    "knight_slash/normal_combo3/frame5"
                )
            ).displayScale(1, 1, 1),
            new ItemDisplayAnimator(
                2,
                List.of(
                    "knight_slash/normal_combo4/frame1",
                    "knight_slash/normal_combo4/frame2",
                    "knight_slash/normal_combo4/frame3",
                    "knight_slash/normal_combo4/frame4",
                    "knight_slash/normal_combo4/frame5",
                    "knight_slash/normal_combo4/frame6",
                    "knight_slash/normal_combo4/frame7"
                )
            ).displayScale(1, 1, 0) // Animatorのコンストラクタに角度情報渡してもいいのでは？あるいはフレームスクラス作ってanimate()時に渡す<-これ前向き
        );

        @Override
        public @NotNull ItemDisplayAnimator getAnimator(@Range(from = 1, to = 5) int currentComboCount) {
            return animators.get(currentComboCount - 1);
        }
    };
}
