package com.gmail.subnokoii.testplugin.lib.vector;

public class VectorLocalAxes {
    private final VectorBuilder x;

    private final VectorBuilder y;

    private final VectorBuilder z;

    public VectorLocalAxes(VectorBuilder forward) throws VectorUnexpectedDimensionSizeException {
        if (forward.getDimensionSize().getValue() != 3) {
            throw new VectorUnexpectedDimensionSizeException();
        }

        z = forward.copy().normalized();

        x = new VectorBuilder(z.getComponent(2), 0, -z.getComponent(0)).normalized();

        try {
            y = forward.cross(x);
        }
        catch (VectorUnexpectedDimensionSizeException e) {
            throw new RuntimeException();
        }
    }

    public VectorBuilder x() {
        return x;
    }

    public VectorBuilder y() {
        return y;
    }

    public VectorBuilder z() {
        return z;
    }
}
