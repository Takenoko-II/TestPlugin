package com.gmail.subnokoii78.testplugin.system.combat.animation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public final class FrameGroup {
    private final List<String> paths = new ArrayList<>();

    private UnaryOperator<AnimatorDisplayState> stateModifier = v -> v;

    private FrameGroup() {}

    public @NotNull List<String> getPaths() {
        return paths;
    }

    public @NotNull UnaryOperator<AnimatorDisplayState> stateModifier() {
        return stateModifier;
    }

    public @NotNull FrameGroup stateModifier(@NotNull UnaryOperator<AnimatorDisplayState> animatorStateModifier) {
        this.stateModifier = animatorStateModifier;
        return this;
    }

    public static @NotNull FrameGroup ofPaths(@NotNull String... paths) {
        final FrameGroup chain = new FrameGroup();

        chain.paths.addAll(Arrays.stream(paths).toList());

        return chain;
    }
}
