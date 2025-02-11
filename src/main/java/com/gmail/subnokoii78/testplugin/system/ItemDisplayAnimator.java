package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
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
            ItemDisplay.class, entity -> {
                entity.setBrightness(new Display.Brightness(15, 15));

                final Transformation transformation = entity.getTransformation();
                final ItemStack itemStack = new ItemStack(Material.KNOWLEDGE_BOOK);

                final AnimatorDisplayState newState = group.stateModifier().apply(state.copy());
                newState.rotation(newState.rotation().roll(newState.rotation().roll() + 90f));

                transformation.getScale().set(newState.scale().toBukkitVector().toVector3f());

                // displayのscaleとrotationからboxつくるメソッドもフレームチェーンクラスに欲しい

                transformation.getLeftRotation().set(newState.rotation().getQuaternion4d());

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
        new GameTickScheduler(__scheduler__ -> {
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
                __scheduler__.runTimeout(frameTime);
            }
            else {
                display.remove();
            }
        }).runTimeout();

        return group.stateModifier().apply(state.copy());
    }
}
