package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.execute.EntityAnchor;
import com.gmail.subnokoii78.util.execute.Execute;
import com.gmail.subnokoii78.util.execute.SourceOrigin;
import com.gmail.subnokoii78.util.execute.SourceStack;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Combo {
    private final int maxCombo;

    private final int timeToReset;

    private final int coolTime;

    protected Combo(int maxCombo, int timeToReset, int coolTime) {
        this.maxCombo = maxCombo;
        this.timeToReset = timeToReset;
        this.coolTime = coolTime;
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

    public void onComboProgress(@NotNull Player player, int currentComboCount) {

    }

    public void onComboIsInCT(@NotNull Player player) {

    }

    public void onComboComplete(@NotNull Player player) {

    }

    public void onComboStop(@NotNull Player player) {

    }

    public static final Combo KNIGHT_NORMAL_SLASH = new Combo(4, 30, 6) {
        @Override
        public void onComboProgress(@NotNull Player player, int currentComboCount) {
            final PlayerComboHandler handler = PlayerComboHandler.getHandler(player);
            final TiltedBoundingBox box = new TiltedBoundingBox(4, 0.5, 2);

            new Execute(new SourceStack(SourceOrigin.of(player)))
                .anchored(EntityAnchor.EYES)
                .positioned.$("^ ^ ^1")
                .run.callback(stack -> {
                    // 適切な位置にボックス設置
                    box.put(stack.getLocation());

                    // box.rotation(roll);

                    // 外枠表示
                    box.showOutline(Color.RED);

                    // ボックスに衝突しているエンティティを取得
                    final Set<Entity> entities = new HashSet<>(box.getCollidingEntities());
                    // プレイヤー本人は除外
                    entities.remove(player);

                    if (entities.isEmpty()) {
                        // 衝突してるエンティティいなければコンボ止める
                        handler.stopCombo();

                        return Execute.FAILURE;
                    }
                    else {
                        entities.forEach(entity -> {
                            if (entity instanceof Damageable damageable) {
                                damageable.damage(2, player);
                            }
                        });

                        ANIMATOR.put(stack.getLocation());
                        ANIMATOR.animate(COMBO1);

                        return Execute.SUCCESS;
                    }
                });

            player.sendMessage(Component.text("コンボ数: " + currentComboCount));
        }

        @Override
        public void onComboIsInCT(@NotNull Player player) {
            player.sendMessage(Component.text("CT中").color(NamedTextColor.GRAY));
        }

        @Override
        public void onComboComplete(@NotNull Player player) {
            player.sendMessage(Component.text("コンボ完成").color(NamedTextColor.GREEN));
        }

        @Override
        public void onComboStop(@NotNull Player player) {
            player.sendMessage(Component.text("コンボ中断").color(NamedTextColor.RED));
        }

        private final AnimationFrameChain COMBO1 = AnimationFrameChain.newChain(
            "knight_slash/normal_combo1/frame1",
            "knight_slash/normal_combo1/frame2",
            "knight_slash/normal_combo1/frame3",
            "knight_slash/normal_combo1/frame4",
            "knight_slash/normal_combo1/frame5"
        ).modifier((pos, rot) -> rot.roll(-60f));

        private final List<String> FRAMES_COMBO2 = List.of(
            "knight_slash/normal_combo2/frame1",
            "knight_slash/normal_combo2/frame2",
            "knight_slash/normal_combo2/frame3",
            "knight_slash/normal_combo2/frame4",
            "knight_slash/normal_combo2/frame5"
        );

        private final List<String> FRAMES_COMBO3 = List.of(
            "knight_slash/normal_combo3/frame1",
            "knight_slash/normal_combo3/frame2",
            "knight_slash/normal_combo3/frame3",
            "knight_slash/normal_combo3/frame4",
            "knight_slash/normal_combo3/frame5"
        );

        private final List<String> FRAMES_COMBO4 = List.of(
            "knight_slash/normal_combo4/frame1",
            "knight_slash/normal_combo4/frame2",
            "knight_slash/normal_combo4/frame3",
            "knight_slash/normal_combo4/frame4",
            "knight_slash/normal_combo4/frame5",
            "knight_slash/normal_combo4/frame6",
            "knight_slash/normal_combo4/frame7"
        );

        private final ItemDisplayAnimator ANIMATOR = new ItemDisplayAnimator(2).displayScale(2, 2, 0);
    };
}
