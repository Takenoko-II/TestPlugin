package com.gmail.subnokoii78.testplugin.particles;

import com.gmail.subnokoii78.util.other.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public final class ParticleDisplay {
    private final Key font;

    private final Transformation transformation;

    private final int frameTime;

    private final int maxFrames;

    private int currentValue = 1;

    private final GameTickScheduler scheduler = new GameTickScheduler(this::next);

    private final Set<TextDisplay> displays = new HashSet<>();

    private ParticleDisplay(@NotNull Key font, @NotNull Transformation transformation, int frameTime, int maxFrames) {
        this.font = font;
        this.transformation = transformation;
        this.frameTime = frameTime;
        this.maxFrames = maxFrames;
    }

    private void next() {
        if (currentValue < maxFrames) {
            displays.forEach(display -> display.text(Component.text(currentValue++).font(font)));
            scheduler.runTimeout(frameTime);
        }
        else {
            displays.forEach(TextDisplay::remove);
        }
    }

    public void play(@NotNull Player player) {
        scheduler.clear();
        final Vector3Builder location = Vector3Builder.from(player);
        final TextDisplay left = player.getWorld().spawn(location.withWorld(player.getWorld()), TextDisplay.class);
        final TextDisplay right = player.getWorld().spawn(location.withRotationAndWorld(new DualAxisRotationBuilder().invert(), player.getWorld()), TextDisplay.class);
        displays.add(left);
        displays.add(right);
        left.setTransformation(transformation);
        right.setTransformation(transformation);
        next();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Key font = Key.key(Key.MINECRAFT_NAMESPACE, "default");

        private int frameTime;

        private int maxFrames;

        private final Transformation transformation = new Transformation(
            new Vector3f(0, 0, 0),
            new Quaternionf(0, 0, 0, 1),
            new Vector3f(1, 1, 1),
            new Quaternionf(0, 0, 0, 1)
        );

        private Builder() {}

        public Builder font(@NotNull String font) {
            if (!Key.parseableValue(font)) {
                throw new IllegalArgumentException();
            }

            this.font = Key.key(Key.MINECRAFT_NAMESPACE, font);
            return this;
        }

        public Builder frameTime(int frameTime) {
            this.frameTime = frameTime;
            return this;
        }

        public Builder maxFrames(int maxFrames) {
            this.maxFrames = maxFrames;
            return this;
        }

        public Builder offset(@NotNull Vector3Builder offset) {
            transformation.getTranslation().set(offset.x(), offset.y(), offset.z());
            return this;
        }

        public Builder scale(@NotNull Vector3Builder scale) {
            transformation.getScale().set(scale.x(), scale.y(), scale.z());
            return this;
        }

        public Builder leftRotation(@NotNull TripleAxisRotationBuilder rotation) {
            transformation.getLeftRotation().set(rotation.getQuaternion4d());
            return this;
        }

        public Builder rightRotation(@NotNull TripleAxisRotationBuilder rotation) {
            transformation.getRightRotation().set(rotation.getQuaternion4d());
            return this;
        }

        public ParticleDisplay build() {
            return new ParticleDisplay(font, transformation, frameTime, maxFrames);
        }
    }
}
