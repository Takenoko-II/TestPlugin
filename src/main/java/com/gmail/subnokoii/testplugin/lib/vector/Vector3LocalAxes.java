package com.gmail.subnokoii.testplugin.lib.vector;

public class Vector3LocalAxes {
    private final Vector3Builder x;
    private final Vector3Builder y;
    private final Vector3Builder z;

    public Vector3LocalAxes(Vector3Builder forward) {
        z = forward.copy().normalized();

        x = new Vector3Builder(z.getComponent(2), 0, -z.getComponent(0))
        .normalized();

        y = z.cross(x);
    }

    public Vector3Builder getX() {
        return x;
    }

    public Vector3Builder getY() {
        return y;
    }

    public Vector3Builder getZ() {
        return z;
    }
}
