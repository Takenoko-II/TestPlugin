package com.gmail.subnokoii.testplugin.lib.vector;

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

    public static final class LocalAxesQ {
        private final EulerQuaternionBuilder quaternion;

        private LocalAxesQ(EulerQuaternionBuilder eulerQuaternionBuilder) {
            quaternion = eulerQuaternionBuilder;
        }

        /**
         * @deprecated
         */
        public Vector3Builder getX() {
            return new RotationBuilder(quaternion.yaw, quaternion.pitch)
            .getDirection3d()
            .getLocalAxes()
            .getX()
            .getRotation2d()
            .pitch(-quaternion.roll)
            .getDirection3d();
        }

        public Vector3Builder getY() {
            return new RotationBuilder(quaternion.yaw, quaternion.pitch)
            .getDirection3d().getLocalAxes().getY();
        }

        public Vector3Builder getZ() {
            return new RotationBuilder(quaternion.yaw, quaternion.pitch)
            .getDirection3d();
        }
    }
}
