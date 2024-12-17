package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.execute.DimensionProvider;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemDisplayAnimator {
    private final int frameTime;

    private World dimension = DimensionProvider.OVERWORLD.getWorld();

    private final Vector3Builder position = new Vector3Builder();

    private final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    private final Vector3Builder scale = new Vector3Builder();

    public ItemDisplayAnimator(int frameTime) {
        this.frameTime = frameTime;
    }

    public final int getFrameTime() {
        return frameTime;
    }

    public final void put(@NotNull Location location) {
        dimension = location.getWorld();
        position.x(location.x()).y(location.y()).z(location.z());
        rotation.yaw(location.getYaw()).pitch(location.getPitch());
    }

    public final @NotNull ItemDisplayAnimator displayScale(double width, double height, double depth) {
        scale.x(width).y(height).z(depth);
        return this;
    } // ここもフレームチェーンクラスに任せたい

    public final void animate(@NotNull AnimationFrameChain frameChain) {
        final ItemDisplay display = dimension.spawn(
            position.withWorld(dimension),
            ItemDisplay.class, entity -> {
                final Transformation transformation = entity.getTransformation();
                final ItemStack itemStack = new ItemStack(Material.KNOWLEDGE_BOOK);

                transformation.getScale().set(scale.toBukkitVector().toVector3f());

                final Vector3Builder pos = position.copy();
                final TripleAxisRotationBuilder rot = rotation.copy();
                frameChain.getModifier().accept(pos, rot); // modifierが個別に欲しい あとめんどくさいからmodifier(T) -> void から modifier(T) -> Tで頼む
                // displayのscaleとrotationからboxつくるメソッドもフレームチェーンクラスに欲しい

                transformation.getTranslation().set(pos.toBukkitVector().toVector3f());
                transformation.getLeftRotation().set(rot.getQuaternion4d());

                entity.setTransformation(transformation);
                entity.setItemStack(itemStack);

                entity.addScoreboardTag(TestPlugin.INTERNAL_ENTITY_TAG);
            }
        );

        final List<NamespacedKey> frames = frameChain.getFrames();

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
