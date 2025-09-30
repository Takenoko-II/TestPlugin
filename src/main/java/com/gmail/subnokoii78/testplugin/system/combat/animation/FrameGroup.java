package com.gmail.subnokoii78.testplugin.system.combat.animation;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

@NullMarked
public final class FrameGroup {
    private final List<String> paths = new ArrayList<>();

    private UnaryOperator<AnimatorDisplayState> stateModifier = v -> v;

    private FrameGroup() {}

    public List<String> getPaths() {
        return paths;
    }

    public UnaryOperator<AnimatorDisplayState> stateModifier() {
        return stateModifier;
    }

    public FrameGroup stateModifier(UnaryOperator<AnimatorDisplayState> animatorStateModifier) {
        this.stateModifier = animatorStateModifier;
        return this;
    }

    public static FrameGroup ofPaths(String... paths) {
        final FrameGroup chain = new FrameGroup();

        chain.paths.addAll(Arrays.stream(paths).toList());

        return chain;
    }
}
