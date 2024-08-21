package com.gmail.subnokoii78.util.vector;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.function.Consumer;

@Deprecated
public final class Shape {
    private final ShapeType type;

    private final Vector3Builder.LocalAxisProvider axes;

    private float scale = 1f;

    private float density = 1f;

    private float angle = 0f;

    private ParticleDecoration decoration = new ParticleDecoration(Particle.FLAME);

    public Shape(ShapeType type, DualAxisRotationBuilder rotation) {
        this.type = type;
        this.axes = rotation.getDirection3d().getLocalAxisProvider();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public ParticleDecoration getParticleDecoration() {
        return decoration;
    }

    public void setParticleDecoration(ParticleDecoration decoration) {
        this.decoration = decoration;
    }

    public void rotate(float angle) {
        this.angle += angle;
    }

    public void draw(World world, Vector3Builder location) {
        forEach(location, vector3 -> {
            if (decoration instanceof DustDecoration) {
                world.spawnParticle(
                    decoration.particle,
                    vector3.withWorld(world),
                    decoration.count,
                    decoration.offset.x(), decoration.offset.y(), decoration.offset.z(),
                    decoration.speed,
                    new Particle.DustOptions(
                        ((DustDecoration) decoration).color,
                        ((DustDecoration) decoration).size
                    )
                );
            }
            else if (decoration instanceof DustTransitionDecoration) {
                world.spawnParticle(
                    decoration.particle,
                    vector3.withWorld(world),
                    decoration.count,
                    decoration.offset.x(), decoration.offset.y(), decoration.offset.z(),
                    decoration.speed,
                    new Particle.DustTransition(
                        ((DustTransitionDecoration) decoration).fromColor,
                        ((DustTransitionDecoration) decoration).toColor,
                        ((DustTransitionDecoration) decoration).size
                    )
                );
            }
            else {
                world.spawnParticle(
                    decoration.particle,
                    vector3.withWorld(world),
                    decoration.count,
                    decoration.offset.x(), decoration.offset.y(), decoration.offset.z(),
                    decoration.speed
                );
            }
        });
    }

    private void forEach(Vector3Builder from, Consumer<Vector3Builder> consumer) {
        switch (type) {
            case STRAIGHT_LINE:
                line(from, from.copy().add(axes.getZ().length(scale)), (int) (scale * 4), consumer);
                break;
            case PERFECT_CIRCLE:
                circle(from, consumer);
                break;
            case TRIANGLE:
            case PENTAGRAM:
            case SEVEN_POINTED_STAR:
            case EIGHT_POINTED_STAR:
            case NINE_POINTED_STAR:
            case TWELVE_POINTED_STAR:
                star(from, type.n, type.k, consumer);
                break;
        }
    }

    public Vector3Builder getPointOnCircle(Vector3Builder center, float angle) {
        double rad = (angle + this.angle) * Math.PI / 180;

        return center.copy()
        .add(axes.getX().scale(scale * Math.cos(rad)))
        .add(axes.getY().scale(scale * Math.sin(rad)));
    }

    private void circle(Vector3Builder center, Consumer<Vector3Builder> consumer) {
        for (int i = 0; i < 360; i++) {
            if (Math.random() > this.density) continue;
            final Vector3Builder vector3Builder = getPointOnCircle(center, i);
            consumer.accept(vector3Builder);
        }
    }

    private void star(Vector3Builder center, int n, int k, Consumer<Vector3Builder> consumer) {
        final Vector3Builder[] points = new Vector3Builder[n];

        for (int i = 0; i < n; i++) {
            points[i] = getPointOnCircle(center, 360f / n * (i + 1));
        }

        final Vector3Builder[] sortedPoints = new Vector3Builder[n];

        int index = k;
        for (int i = 0; i < points.length; i++) {
            index += k;
            if (index >= n) index -= n;

            sortedPoints[i] = points[index];
        }

        for (int j = 0; j < sortedPoints.length; j++) {
            final Vector3Builder current = sortedPoints[j];
            final Vector3Builder next;

            if (j == sortedPoints.length - 1) next = sortedPoints[0];
            else next = sortedPoints[j + 1];

            line(current, next, 30, consumer);
        }
    }

    private void line(Vector3Builder from, Vector3Builder to, int count, Consumer<Vector3Builder> consumer) {
        for (int i = 0; i < count; i++) {
            if (Math.random() > this.density) continue;
            final Vector3Builder v = from.lerp(to, (float) i / (float) count);
            consumer.accept(v);
        }
    }

    public enum ShapeType {
        STRAIGHT_LINE(2, 0),

        PERFECT_CIRCLE(Integer.MAX_VALUE, 0),

        TRIANGLE(6, 2),

        PENTAGRAM(5, 2),

        SEVEN_POINTED_STAR(7, 3),

        EIGHT_POINTED_STAR(8, 3),

        NINE_POINTED_STAR(9, 4),

        TWELVE_POINTED_STAR(12, 5);

        private final int n;

        private final int k;

        ShapeType(int n, int k) {
            this.n = n;
            this.k = k;
        }
    }

    public static class ParticleDecoration {
        private final Particle particle;

        private int count = 1;

        private double speed = 0.0d;

        private final Vector3Builder offset = new Vector3Builder();

        public ParticleDecoration(Particle particle) {
            this.particle = particle;
        }

        public Particle getParticle() {
            return particle;
        }

        public int getCount() {
            return count;
        }

        public ParticleDecoration setCount(int count) {
            this.count = count;
            return this;
        }

        public double getSpeed() {
            return speed;
        }

        public ParticleDecoration setSpeed(double speed) {
            this.speed = speed;
            return this;
        }

        public Vector3Builder getOffset() {
            return offset;
        }
    }

    public static class DustDecoration extends ParticleDecoration {
        private Color color = Color.BLACK;

        private float size = 1.0f;

        public DustDecoration() {
            super(Particle.DUST);
        }

        public Color getColor() {
            return color;
        }

        public DustDecoration setColor(Color color) {
            this.color = color;
            return this;
        }

        public float getSize() {
            return size;
        }

        public DustDecoration setSize(float size) {
            this.size = size;
            return this;
        }
    }

    public static class DustTransitionDecoration extends ParticleDecoration {
        private Color fromColor = Color.BLACK;

        private Color toColor = Color.WHITE;

        private float size = 1.0f;

        public DustTransitionDecoration() {
            super(Particle.DUST_COLOR_TRANSITION);
        }

        public Color getFromColor() {
            return fromColor;
        }

        public DustTransitionDecoration setFromColor(Color color) {
            this.fromColor = color;
            return this;
        }

        public Color getToColor() {
            return toColor;
        }

        public DustTransitionDecoration setToColor(Color toColor) {
            this.toColor = toColor;
            return this;
        }

        public float getSize() {
            return size;
        }

        public DustTransitionDecoration setSize(float size) {
            this.size = size;
            return this;
        }
    }
}
