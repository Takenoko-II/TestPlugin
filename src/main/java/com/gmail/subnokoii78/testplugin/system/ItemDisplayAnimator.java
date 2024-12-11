package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDisplayAnimator {
    private final int frameTime;

    private final Vector3Builder scale = new Vector3Builder();

    private final List<NamespacedKey> frames = new ArrayList<>();

    public ItemDisplayAnimator(int frameTime, @NotNull List<String> frames) {
        this.frameTime = frameTime;
        this.frames.addAll(Arrays.stream(frames.toArray(String[]::new)).map(frame -> {
            final NamespacedKey key = NamespacedKey.fromString(frame);

            if (key == null) {
                throw new IllegalArgumentException("フレームのテクスチャパスの形式が正しくありません");
            }

            return key;
        }).toList());
    }

    public final int getFrameTime() {
        return frameTime;
    }

    public final @NotNull List<NamespacedKey> getFrames() {
        return List.copyOf(frames);
    }

    public final @NotNull ItemDisplayAnimator displayScale(double width, double height, double depth) {
        scale.x(width).y(height).z(depth);
        return this;
    }

    public final void animateAt(@NotNull World dimension, @NotNull Vector3Builder position, @NotNull TripleAxisRotationBuilder rotation) {
        final ItemDisplay display = dimension.spawn(
            position.withRotationAndWorld(rotation.getRotation2d(), dimension),
            ItemDisplay.class, entity -> {
                final Transformation transformation = entity.getTransformation();
                final ItemStack itemStack = new ItemStack(Material.KNOWLEDGE_BOOK);

                transformation.getScale().set(scale.toBukkitVector().toVector3f());
                transformation.getLeftRotation().set(rotation.getQuaternion4d());

                entity.setTransformation(transformation);
                entity.setItemStack(itemStack);

                entity.addScoreboardTag(TestPlugin.INTERNAL_ENTITY_TAG);
            }
        );

        final int[] index = {0};
        new GameTickScheduler(__scheduler__ -> {
            if (index[0] < frames.size()) {
                final ItemStack itemStack = display.getItemStack();
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setItemModel(frames.get(index[0]));
                itemStack.setItemMeta(itemMeta);
                display.setItemStack(itemStack);

                index[0]++;
                __scheduler__.runTimeout(frameTime);
            }
            else {
                display.remove();
            }
        }).runTimeout();
    }
}
