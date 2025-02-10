package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.execute.DimensionProvider;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class AnimatorDisplayState {
    private World dimension = DimensionProvider.OVERWORLD.getWorld();

    private final Vector3Builder position = new Vector3Builder();

    private final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    private final Vector3Builder scale = new Vector3Builder(1, 1, 1);

    public @NotNull World dimension() {
        return dimension;
    }

    public void dimension(@NotNull World dimension) {
        this.dimension = dimension;
    }

    public @NotNull Vector3Builder position() {
        return this.position.copy();
    }

    public void position(@NotNull Vector3Builder position) {
        this.position.x(position.x()).y(position.y()).z(position.z());
    }

    public @NotNull TripleAxisRotationBuilder rotation() {
        return this.rotation.copy();
    }

    public void rotation(@NotNull TripleAxisRotationBuilder rotation) {
        this.rotation.yaw(rotation.yaw()).pitch(rotation.pitch()).roll(rotation.roll());
    }

    public @NotNull Vector3Builder scale() {
        return this.scale.copy();
    }

    public void scale(@NotNull Vector3Builder scale) {
        this.scale.x(scale.x()).y(scale.y()).z(scale.z());
    }

    public @NotNull AnimatorDisplayState copy() {
        final var state = new AnimatorDisplayState();
        state.dimension(dimension);
        state.position(position);
        state.rotation(rotation);
        state.scale(scale);
        return state;
    }
}
