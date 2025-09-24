package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.testplugin.PluginConfigurationManager;
import com.gmail.subnokoii78.testplugin.util.Converter;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.subnokoii78.tplcore.execute.EntityAnchor;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.execute.SourceOrigin;
import com.gmail.subnokoii78.tplcore.vector.OrientedBoundingBox;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONNumber;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Combo {
    private final String id;

    private final int maxCombo;

    private final int timeToReset;

    private final int coolTime;

    protected Combo(@NotNull String id, int maxCombo, int timeToReset, int coolTime) {
        this.id = id;
        this.maxCombo = maxCombo;
        this.timeToReset = timeToReset;
        this.coolTime = coolTime;
    }

    public final @NotNull String getId() {
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

    public void onComboProgress(@NotNull Player player, int currentComboCount) {

    }

    public void onComboIsInCT(@NotNull Player player) {

    }

    public void onComboComplete(@NotNull Player player) {

    }

    public void onComboStop(@NotNull Player player) {

    }

    public static final Combo KNIGHT_NORMAL_SLASH = new Combo("knight_slash", 4, 30, 6) {
        @Override
        public void onComboProgress(@NotNull Player player, int currentComboCount) {
            final PlayerComboHandler handler = PlayerComboHandler.getHandler(player);
            final OrientedBoundingBox box = new OrientedBoundingBox(4, 0.5, 2);

            new Execute(new CommandSourceStack(player))
                .anchored(EntityAnchor.EYES)
                .positioned.$("^ ^ ^1")
                .run.callback(stack -> {
                    ANIMATOR.put(stack.getLocation());
                    final AnimatorDisplayState state = ANIMATOR.animate(currentComboCount);

                    box.put(stack.getLocation());
                    box.rotation(state.rotation());
                    box.showOutline(Color.BLUE);

                    final Set<Entity> entities = new HashSet<>(box.getCollidingEntities())
                        .stream().filter(entity -> {
                            if (entity instanceof Mob || entity instanceof Player) {
                                return !entity.equals(player);
                            }
                            else return false;
                        })
                        .collect(Collectors.toSet());

                    if (entities.isEmpty()) {
                        handler.stopCombo();
                        return Execute.FAILURE;
                    }
                    else {
                        entities.forEach(entity -> {
                            if (entity instanceof Damageable damageable) {
                                damageable.damage(2, player);
                            }
                        });

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

        private final FrameGroup COMBO1 = FrameGroup.ofPaths(
            "normal_combo1/frame1",
            "normal_combo1/frame2",
            "normal_combo1/frame3",
            "normal_combo1/frame4",
            "normal_combo1/frame5"
        ).stateModifier(state -> {
            final float combo1Angle = PluginConfigurationManager.getOrWriteDefault(
                JSONPath.of("combos.knight.normal_slash.angles[0]"),
                JSONValueTypes.NUMBER,
                JSONNumber.valueOf(-60f)
            ).floatValue();

            state.rotation(state.rotation().roll(combo1Angle));
            return state;
        });

        private final FrameGroup COMBO2 = FrameGroup.ofPaths(
            "normal_combo2/frame1",
            "normal_combo2/frame2",
            "normal_combo2/frame3",
            "normal_combo2/frame4",
            "normal_combo2/frame5"
        ).stateModifier(state -> {
            final float combo2Angle = PluginConfigurationManager.getOrWriteDefault(
                JSONPath.of("combos.knight.normal_slash.angles[1]"),
                JSONValueTypes.NUMBER,
                JSONNumber.valueOf(30f)
            ).floatValue();

            state.rotation(state.rotation().roll(combo2Angle));
            return state;
        });

        private final FrameGroup COMBO3 = FrameGroup.ofPaths(
            "normal_combo3/frame1",
            "normal_combo3/frame2",
            "normal_combo3/frame3",
            "normal_combo3/frame4",
            "normal_combo3/frame5"
        ).stateModifier(state -> {
            final float combo3Angle = PluginConfigurationManager.getOrWriteDefault(
                JSONPath.of("combos.knight.normal_slash.angles[2]"),
                JSONValueTypes.NUMBER,
                JSONNumber.valueOf(70f)
            ).floatValue();

            state.rotation(state.rotation().roll(combo3Angle));
            return state;
        });

        private final FrameGroup COMBO4 = FrameGroup.ofPaths(
            "normal_combo4/frame1",
            "normal_combo4/frame2",
            "normal_combo4/frame3",
            "normal_combo4/frame4",
            "normal_combo4/frame5",
            "normal_combo4/frame6",
            "normal_combo4/frame7"
        ).stateModifier(state -> {
            final float combo4Angle = PluginConfigurationManager.getOrWriteDefault(
                JSONPath.of("combos.knight.normal_slash.angles[3]"),
                JSONValueTypes.NUMBER,
                JSONNumber.valueOf(0f)
            ).floatValue();

            state.rotation(state.rotation().roll(combo4Angle));
            return state;
        });

        private final ItemDisplayAnimator ANIMATOR = new ItemDisplayAnimator(getId(), 2)
            .defaultScale(Converter.jsonArrayToVector3(
                PluginConfigurationManager.getOrWriteDefault(
                    JSONPath.of("combos.knight.normal_slash.display_scale"),
                    JSONValueTypes.ARRAY,
                    new JSONArray(List.of(
                        JSONNumber.valueOf(3),
                        JSONNumber.valueOf(6),
                        JSONNumber.valueOf(0.1)
                    ))
                ).typed(JSONValueTypes.NUMBER)
            ))
            .addFrameGroup(COMBO1)
            .addFrameGroup(COMBO2)
            .addFrameGroup(COMBO3)
            .addFrameGroup(COMBO4);
    };
}
