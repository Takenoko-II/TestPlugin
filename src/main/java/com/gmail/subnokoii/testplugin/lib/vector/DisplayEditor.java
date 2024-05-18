package com.gmail.subnokoii.testplugin.lib.vector;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4d;
import org.w3c.dom.Text;

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

    public DisplayEditor rotateLeft(Vector3Builder axis, float degree) {
        transformation(t -> {
            t.getLeftRotation().set(new AxisAngle4d(
            degree * Math.PI / 180,
            (float) axis.x(),
            (float) axis.y(),
            (float) axis.z()
            ));
        });

        return this;
    }

    public DisplayEditor rotateRight(Vector3Builder axis, float degree) {
        transformation(t -> {
            t.getRightRotation().set(new AxisAngle4d(
            degree * Math.PI / 180,
            (float) axis.x(),
            (float) axis.y(),
            (float) axis.z()
            ));
        });

        return this;
    }

    public DisplayEditor move(Vector3Builder vector) {
        transformation(t -> {
            t.getTranslation().add((float) vector.x(), (float) vector.y(), (float) vector.z());
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
