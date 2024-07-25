package com.gmail.subnokoii78.util.shape;

import com.gmail.subnokoii78.util.vector.EntireAxisRotationHandler;
import com.gmail.subnokoii78.util.vector.Vector3Builder;

public class Rhombus extends ShapeBase {
    @Override
    public void draw() {
        final Vector3Builder.LocalAxes axes = rotation.getLocalAxesE();

        var left = axes.getX();
        var right = axes.getX().inverted();
        var up = axes.getY();
        var down = axes.getY().inverted();

        lineFromTo(down, left);
        lineFromTo(left, up);
        lineFromTo(up,right);
        lineFromTo(right, down);
    }

    protected void lineFromTo(Vector3Builder from, Vector3Builder to) {
        final StraightLine line = new StraightLine();
        line.put(world, from);
        line.onDot(this::dot);
        line.rotate(EntireAxisRotationHandler.from(from.getDirectionTo(to).getRotation2d()));
        line.setScale((float) from.getDistanceBetween(to));
        line.draw();
    }
}
