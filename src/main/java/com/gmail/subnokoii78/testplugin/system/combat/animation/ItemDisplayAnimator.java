package com.gmail.subnokoii78.testplugin.system.combat.animation;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.util.Converter;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONString;
import com.gmail.takenokoii78.json.values.TypedJSONArray;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemDisplayAnimator {
    private final @NotNull String id;

    private final int frameTime;

    private final List<FrameGroup> groups = new ArrayList<>();

    private final AnimatorDisplayState state = new AnimatorDisplayState();

    public ItemDisplayAnimator(@NotNull String id, int frameTime) {
        this.id = id;
        this.frameTime = frameTime;
    }

    public final int getFrameTime() {
        return frameTime;
    }

    public final @NotNull ItemDisplayAnimator addFrameGroup(@NotNull FrameGroup group) {
        groups.add(group);
        return this;
    }

    public final void put(@NotNull Location location) {
        state.dimension(location.getWorld());
        state.position(Vector3Builder.from(location));
        state.rotation(
            state.rotation().yaw(location.getYaw()).pitch(location.getPitch()).roll(state.rotation().roll())
        );
    }

    public final @NotNull ItemDisplayAnimator defaultScale(@NotNull Vector3Builder scale) {
        state.scale(scale);
        return this;
    }

    public final @NotNull AnimatorDisplayState animate(int id) {
        final FrameGroup group = groups.get(id - 1);

        final ItemDisplay display = state.dimension().spawn(
            state.position().withWorld(state.dimension()),
            ItemDisplay.class,
            entity -> {
                entity.setBrightness(new Display.Brightness(15, 15));

                final Transformation transformation = entity.getTransformation();
                final ItemStack itemStack = new ItemStack(Material.KNOWLEDGE_BOOK);

                final AnimatorDisplayState newState = group.stateModifier().apply(state.copy()); // here!!!!
                newState.rotation(newState.rotation().roll(newState.rotation().roll() + 90f));

                transformation.getScale().set(newState.scale().toBukkitVector().toVector3f());
                // displayのscaleとrotationからboxつくるメソッドもフレームチェーンクラスに欲しい
                transformation.getLeftRotation().set(newState.rotation().getQuaternion4f());

                entity.setTransformation(transformation);
                entity.setItemStack(itemStack);
                entity.addScoreboardTag(TestPlugin.INTERNAL_ENTITY_TAG);
            }
        );

        final List<String> frames = group.getPaths();
        final NamespacedKey key = NamespacedKey.fromString(this.id);

        if (key == null) {
            throw new IllegalStateException("ItemDisplayAnimatorのidが無効です");
        }

        final int[] index = {0};
        new GameTickScheduler(scheduler -> {
            if (index[0] < frames.size()) {
                final ItemStack itemStack = display.getItemStack();

                itemStack.setData(DataComponentTypes.ITEM_MODEL, key);
                itemStack.setData(
                    DataComponentTypes.CUSTOM_MODEL_DATA,
                    CustomModelData.customModelData()
                        .addString(frames.get(index[0]))
                        .build()
                );

                display.setItemStack(itemStack);

                index[0]++;
                scheduler.runTimeout(frameTime);
            }
            else {
                display.remove();
            }
        }).runTimeout();

        return group.stateModifier().apply(state.copy());
    }

    public static ItemDisplayAnimator fromConfig(String id) {
        try {
            final JSONObject animations = TPLCore.getPluginConfigLoader().get()
                .get("animations", JSONValueTypes.OBJECT);

            final JSONObject animation = animations.get(id, JSONValueTypes.OBJECT);

            final int frameTime = animation.get("frame_time", JSONValueTypes.NUMBER).intValue();
            final ItemDisplayAnimator animator = new ItemDisplayAnimator(id, frameTime);

            final JSONObject defaultState = animation.get("default_state", JSONValueTypes.OBJECT);
            final Vector3Builder scale = Converter.jsonArrayToVector3(
                defaultState.get("scale", JSONValueTypes.ARRAY).typed(JSONValueTypes.NUMBER)
            );
            animator.defaultScale(scale);

            final TypedJSONArray<JSONObject> combos = animation.get("combos", JSONValueTypes.ARRAY).typed(JSONValueTypes.OBJECT);
            for (final JSONObject combo : combos) {
                final TypedJSONArray<JSONString> frames = combo.get("frames", JSONValueTypes.ARRAY).typed(JSONValueTypes.STRING);
                final List<String> paths = new ArrayList<>();
                frames.forEach(frame -> {
                    paths.add(frame.getValue());
                });
                final FrameGroup frameGroup = FrameGroup.ofPaths(paths.toArray(String[]::new));
                final JSONObject parameters = combo.get("parameters", JSONValueTypes.OBJECT);
                frameGroup.stateModifier(state -> {
                    state.interpret(parameters);
                    return state;
                });
                animator.addFrameGroup(frameGroup);
            }

            return animator;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException("configを読み取れませんでした", e);
        }
    }
}
