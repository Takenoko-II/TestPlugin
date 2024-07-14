package com.gmail.subnokoii.testplugin.lib.vector;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

public final class EulerQuaternionBuilder {
    private final Quaternionf quaternion = new Quaternionf(0f, 0f, 0f, 1f);

    private float yaw = 0;

    private float pitch = 0;

    private float roll = 0;

    public EulerQuaternionBuilder(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        update();
    }

    public EulerQuaternionBuilder() {}

    private void rotateQuaternion(Vector3Builder axis, float angle) {
        final Vector3Builder normalized = axis.copy().normalized();

        quaternion.rotateAxis(
            (float) (angle * Math.PI / 180),
            (float) normalized.x(),
            (float) normalized.y(),
            (float) normalized.z()
        );
    }

    private void update() {
        final Vector3Builder.LocalAxes axes = new RotationBuilder(yaw, pitch).getDirection3d().getLocalAxes();
        quaternion.x = 0;
        quaternion.y = 0;
        quaternion.z = 0;
        quaternion.w = 1;

        TestPlugin.log(TestPlugin.LoggingTarget.SERVER, yaw + ", " + pitch + ", " + roll);
        rotateQuaternion(axes.getZ(), roll);
        rotateQuaternion(axes.getX(), pitch);
        rotateQuaternion(new Vector3Builder(0, 1, 0), -(yaw + 90));
    }

    public EulerQuaternionBuilder rotateYaw(float yaw) {
        this.yaw += yaw;
        update();
        return this;
    }

    public EulerQuaternionBuilder rotatePitch(float pitch) {
        this.pitch += pitch;
        update();
        return this;
    }

    public EulerQuaternionBuilder rotateRoll(float roll) {
        this.roll += roll;
        update();
        return this;
    }

    public EulerQuaternionBuilder add(EulerQuaternionBuilder quaternionBuilder) {
        this.yaw += quaternionBuilder.yaw;
        this.pitch += quaternionBuilder.pitch;
        this.roll += quaternionBuilder.roll;
        update();
        return this;
    }

    public EulerQuaternionBuilder subtract(EulerQuaternionBuilder quaternionBuilder) {
        this.yaw -= quaternionBuilder.yaw;
        this.pitch -= quaternionBuilder.pitch;
        this.roll -= quaternionBuilder.roll;
        update();
        return this;
    }

    public Quaternionf getQuaternion() {
        return quaternion;
    }

    public LocalAxesQ getLocalAxesQ() {
        return new LocalAxesQ(this);
    }

    public RotationBuilder getRotation2d() {
        return new RotationBuilder(yaw, pitch);
    }

    public Display toDisplayLeftRotation(Display display) {
        final Transformation transformation = display.getTransformation();
        transformation.getLeftRotation().set(quaternion);
        display.setTransformation(transformation);

        return display;
    }

    public Display toDisplayRightRotation(Display display) {
        final Transformation transformation = display.getTransformation();
        transformation.getRightRotation().set(quaternion);
        display.setTransformation(transformation);

        return display;
    }

    public static EulerQuaternionBuilder from(RotationBuilder rotation) {
        return new EulerQuaternionBuilder(rotation.yaw(), rotation.pitch(), 0);
    }

    public static EulerQuaternionBuilder from(Entity entity) {
        return new EulerQuaternionBuilder(entity.getYaw(), entity.getPitch(), 0);
    }

    public static final class LocalAxesQ extends Vector3Builder.LocalAxes {
        private final double[][] matrix;

        private LocalAxesQ(EulerQuaternionBuilder eulerQuaternionBuilder) {
            super(eulerQuaternionBuilder.getRotation2d().getDirection3d());

            final double radian = eulerQuaternionBuilder.roll * Math.PI / 180;
            final double sin = Math.sin(radian);
            final double cos = Math.cos(radian);
            final Vector3Builder axis = super.getZ();
            final double x = axis.x();
            final double y = axis.y();
            final double z = axis.z();

            this.matrix = new double[][]{
                new double[]{
                    cos + x * x * (1 - cos),
                    x * y * (1 - cos) - z * sin,
                    x * z * (1 - cos) + y * sin
                },
                new double[]{
                    y * x * (1 - cos) + z * sin,
                    cos + y * y * (1 - cos),
                    y * z * (1 - cos) - x * sin
                },
                new double[]{
                    z * x * (1 - cos) - y * sin,
                    z * y * (1 - cos) + x * sin,
                    cos + z * z * (1 - cos)
                }
            };
        }

        private Vector3Builder rotate(Vector3Builder vector3) {
            final double x = matrix[0][0] * vector3.x() + matrix[0][1] * vector3.y() + matrix[0][2] * vector3.z();
            final double y = matrix[1][0] * vector3.x() + matrix[1][1] * vector3.y() + matrix[1][2] * vector3.z();
            final double z = matrix[2][0] * vector3.x() + matrix[2][1] * vector3.y() + matrix[2][2] * vector3.z();
            return new Vector3Builder(x, y, z);
        }

        public Vector3Builder getX() {
            return rotate(super.getX());
        }

        public Vector3Builder getY() {
            return rotate(super.getY());
        }

        public Vector3Builder getZ() {
            return super.getZ();
        }
    }
}
