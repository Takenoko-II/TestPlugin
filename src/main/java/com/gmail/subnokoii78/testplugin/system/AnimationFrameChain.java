package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public final class AnimationFrameChain {
    private final List<NamespacedKey> frames = new ArrayList<>();

    private BiConsumer<Vector3Builder, TripleAxisRotationBuilder> modifier = (a, b) -> {};

    private AnimationFrameChain() {}

    public @NotNull List<NamespacedKey> getFrames() {
        return List.copyOf(frames);
    }

    public @NotNull BiConsumer<Vector3Builder, TripleAxisRotationBuilder> getModifier() {
        return modifier;
    }

    public @NotNull AnimationFrameChain modifier(@NotNull BiConsumer<Vector3Builder, TripleAxisRotationBuilder> modifier) {
        this.modifier = modifier;
        return this;
    }

    public static @NotNull AnimationFrameChain newChain(@NotNull String... paths) {
        final AnimationFrameChain chain = new AnimationFrameChain();

        chain.frames.addAll(Arrays.stream(paths).map(path -> {
            final NamespacedKey key = NamespacedKey.fromString(path);

            if (key == null) {
                throw new IllegalArgumentException("無効な形式のパスです");
            }

            return key;
        }).toList());

        return chain;
    }
}
