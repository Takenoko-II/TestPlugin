package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.function.Consumer;

public class DisplayEditor {
    private final Display display;

    public DisplayEditor(Entity entity) {
        if (entity instanceof Display) {
            display = (Display) entity;
        }
        else {
            throw new IllegalArgumentException("引数に渡されたエンティティがDisplayではありません");
        }
    }

    public DisplayEditor(Display entity) {
        display = entity;
    }

    private void transformation(Consumer<Transformation> consumer) {
        final Transformation transformation = display.getTransformation();
        consumer.accept(transformation);
        display.setTransformation(transformation);
    }

    public DisplayEditor rotate(Vector3Builder axis, float degree) {
        final Vector3Builder normalized = axis.copy().normalized();

        transformation(t -> {
            t.getLeftRotation().rotateAxis(
                (float) (degree * Math.PI / 180),
                (float) normalized.x(),
                (float) normalized.y(),
                (float) normalized.z()
            );
        });

        return this;
    }

    public DisplayEditor move(Vector3Builder vector3) {
        transformation(t -> {
            t.getTranslation().add((float) vector3.x(), (float) vector3.y(), (float) vector3.z());
        });

        return this;
    }

    public DisplayEditor setScale(Vector3Builder scale) {
        transformation(t -> {
            t.getScale().set(scale.x(), scale.y(), scale.z());
        });

        return this;
    }

    public Display getEntity() {
        return display;
    }

    public static DisplayEditor spawnItemDisplay(Location location, ItemStack itemStack) {
        final ItemDisplay entity = location.getWorld().spawn(location, ItemDisplay.class);
        entity.setItemStack(itemStack);

        return new DisplayEditor(entity);
    }

    public static DisplayEditor spawnBlockDisplay(Location location, BlockData data) {
        final BlockDisplay entity = location.getWorld().spawn(location, BlockDisplay.class);
        entity.setBlock(data);

        return new DisplayEditor(entity);
    }
}
