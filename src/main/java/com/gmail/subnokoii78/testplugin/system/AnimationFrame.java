package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public abstract class AnimationFrame {
    private final NamespacedKey framePath;

    private AnimationFrame(@NotNull String framePath) {
        final NamespacedKey key = NamespacedKey.fromString(framePath);

        if (key == null) {
            throw new IllegalArgumentException("無効な形式のパスです");
        }

        this.framePath = key;
    }

    public final @NotNull NamespacedKey getFramePath() {
        return framePath;
    }

    protected abstract @NotNull TripleAxisRotationBuilder rotationModifier(@NotNull TripleAxisRotationBuilder rotation);
}
