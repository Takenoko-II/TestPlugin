package com.gmail.subnokoii78.util.shape;

import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;

public class Star extends ShapeBase {
    private final int n;

    private final int k;

    protected Star(int n, int k) {
        this.n = n;
        this.k = k;
    }

    @Override
    public void draw() {
        final Vector3Builder[] points = new Vector3Builder[n];
        final PerfectCircle circle = new PerfectCircle();
        circle.put(world);
        circle.rotate(rotation);

        for (int i = 0; i < n; i++) {
            points[i] = circle.getPointOnAngle((360f / n) * i + 90);
        }

        final Vector3Builder[] sortedPoints = new Vector3Builder[n];

        int index = k;
        for (int i = 0; i < points.length; i++) {
            index += k;
            if (index >= n) index -= n;

            sortedPoints[i] = points[index];
        }

        for (int i = 0; i < sortedPoints.length; i++) {
            final Vector3Builder current = sortedPoints[i];
            final Vector3Builder next = (i == sortedPoints.length - 1)
                ? sortedPoints[0]
                : sortedPoints[i + 1];

            final StraightLine line = new StraightLine();
            final Vector3Builder direction = current.getDirectionTo(next);
            line.put(world, current);
            line.rotate(TripleAxisRotationBuilder.from(direction.getRotation2d()));
            line.setScale((float) current.getDistanceTo(next));
            line.setDensity(getDensity());
            line.onDot(this::dot);
            line.draw();
        }
    }
}
