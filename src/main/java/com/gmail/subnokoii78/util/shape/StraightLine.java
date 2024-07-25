package com.gmail.subnokoii78.util.shape;

import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

public class StraightLine extends ShapeBase {
    @Override
    public void draw() {
        final double max = 20 * getDensity();
        for (int i = 0; i < max; i++) {
            dot(rotation.getDirection3d().length(i / max));
        }
    }

    public @Nullable RayTraceResult rayTrace() {
        return world.rayTraceEntities(center.withWorld(world), rotation.getDirection3d().toBukkitVector(), getScale());
    }
}
