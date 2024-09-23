package com.gmail.subnokoii78.testplugin.particles;

import com.gmail.subnokoii78.util.file.TextFileUtils;
import com.gmail.subnokoii78.util.file.json.*;
import com.gmail.subnokoii78.util.other.DisplayEditor;
import com.gmail.subnokoii78.util.other.TupleT;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public final class FontParticleHandler {
    private final TupleT<Key> fonts;

    private final Transformation transformation;

    private final TripleAxisRotationBuilder rotationOffset;

    private final int frameTime;

    private final int maxFrames;

    private int currentValue = 1;

    private final GameTickScheduler scheduler = new GameTickScheduler(this::next);

    private final Set<TextDisplay> displays = new HashSet<>();

    private FontParticleHandler(@NotNull TupleT<Key> fonts, @NotNull Transformation transformation, int frameTime, int maxFrames, @NotNull TripleAxisRotationBuilder rotationOffset) {
        this.fonts = fonts;
        this.transformation = transformation;
        this.frameTime = frameTime;
        this.maxFrames = maxFrames;
        this.rotationOffset = rotationOffset;
    }

    private void next() {
        if (currentValue < maxFrames) {
            final AtomicInteger i = new AtomicInteger();
            displays.forEach(display -> {
                if (i.get() == 0) {
                    display.text(Component.text(currentValue).font(fonts.left()));
                }
                else {
                    display.text(Component.text(currentValue).font(fonts.right()));
                }
                display.setBrightness(new Display.Brightness(15, 15));
                display.setBackgroundColor(Color.fromARGB(0));
                i.getAndIncrement();
            });
            currentValue++;
            scheduler.runTimeout(frameTime);
        }
        else {
            displays.forEach(TextDisplay::remove);
        }
    }

    public void play(@NotNull Player player) {
        scheduler.clear();

        final Vector3Builder location = Vector3Builder.from(player);

        final TextDisplay left = player.getWorld().spawn(location.withRotationAndWorld(new DualAxisRotationBuilder(0, 0), player.getWorld()), TextDisplay.class);
        final TextDisplay right = player.getWorld().spawn(location.withRotationAndWorld(new DualAxisRotationBuilder(180, 0), player.getWorld()), TextDisplay.class);
        displays.add(left);
        displays.add(right);

        final var playerRot = TripleAxisRotationBuilder.from(DualAxisRotationBuilder.from(player));

        left.setTransformation(transformation);
        right.setTransformation(transformation);

        var editorl = new DisplayEditor(left)
            .rotate(playerRot.getDirection3d(), rotationOffset.roll());

        var editor = new DisplayEditor(right)
            .rotate(playerRot.getDirection3d(),-rotationOffset.roll())
            /*.rotate(playerRot.getLocalAxisProviderE().getX(), -rotationOffset.pitch())
            .rotate(new Vector3Builder(0, 1, 0), rotationOffset.yaw())*/;

        transformation.getLeftRotation().set(playerRot.copy().add(rotationOffset.pitch(rotationOffset.pitch() * -1).roll(-(rotationOffset.roll() - 180) + 180)).getQuaternion4d());

        next();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TupleT<Key> fonts = new TupleT<>(
            Key.key(Key.MINECRAFT_NAMESPACE, "default"),
            Key.key(Key.MINECRAFT_NAMESPACE, "default")
        );

        private int frameTime;

        private int maxFrames;

        private final Transformation transformation = new Transformation(
            new Vector3f(0, 0, 0),
            new Quaternionf(0, 0, 0, 1),
            new Vector3f(1, 1, 1),
            new Quaternionf(0, 0, 0, 1)
        );

        private final TripleAxisRotationBuilder rotationOffset = new TripleAxisRotationBuilder();

        private Builder() {}

        public Builder font(@NotNull TupleT<String> fonts) {
            this.fonts = new TupleT<>(
                Key.key(Key.MINECRAFT_NAMESPACE, fonts.left()),
                Key.key(Key.MINECRAFT_NAMESPACE, fonts.right())
            );
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

        public Builder positionOffset(@NotNull Vector3Builder offset) {
            transformation.getTranslation().set(offset.x(), offset.y(), offset.z());
            return this;
        }

        public Builder scale(@NotNull Vector3Builder scale) {
            transformation.getScale().set(scale.x(), scale.y(), scale.z());
            return this;
        }

        public Builder rotationOffset(@NotNull TripleAxisRotationBuilder rotation) {
            rotationOffset.calculate(rotation, (a, b) -> b);
            return this;
        }

        public void buildAndPlay(@NotNull Player player) {
            new FontParticleHandler(fonts, transformation, frameTime, maxFrames, rotationOffset).play(player);
        }
    }

    public static void reload() {
        final JSONFileHandler handler = new JSONFileHandler(path);

        if (!TextFileUtils.exist(path)) {
            TextFileUtils.create(path);
            final var defaultObj = new JSONParser(
                """
                {
                    "knight_slash_fourth": {
                        "font": [
                            "slash/knight/fourth",
                            "slash/knight/fourth_reversed"
                        ],
                        "frame_time": 2,
                        "max_frames": 7,
                        "scale": [16, 16, 16],
                        "offsets": {
                            "position": [0, 1.62, 0],
                            "rotation": [0, 0, 45]
                        }
                    }
                }
                """
            ).parseObject();

            handler.writeObject(defaultObj);

            json = defaultObj;
        }
        else {
            json = handler.readObject();
        }
    }

    private static JSONObject json;

    private static final String path = "plugins/font_particles_definitions.json";

    public static Builder createBuilder(@NotNull String key) {
        if (json == null) reload();

        final var definition = json.get(key, JSONValueType.OBJECT);
        final JSONArray fontsArray = definition.get("font", JSONValueType.ARRAY);
        final String font1 = fontsArray.get(0, JSONValueType.STRING);
        final String font2 = fontsArray.get(1, JSONValueType.STRING);
        final int frameTime = definition.get("frame_time", JSONValueType.NUMBER).intValue();
        final int maxFrames = definition.get("max_frames", JSONValueType.NUMBER).intValue();
        final JSONArray scaleArray = definition.get("scale", JSONValueType.ARRAY);
        final Vector3Builder scale = new Vector3Builder(
            scaleArray.get(0, JSONValueType.NUMBER).doubleValue(),
            scaleArray.get(1, JSONValueType.NUMBER).doubleValue(),
            scaleArray.get(2, JSONValueType.NUMBER).doubleValue()
        );
        final JSONObject offsets = definition.get("offsets", JSONValueType.OBJECT);
        final JSONArray positionOffsetArray = offsets.get("position", JSONValueType.ARRAY);
        final JSONArray rotationOffsetArray = offsets.get("rotation", JSONValueType.ARRAY);
        final Vector3Builder positionOffset = new Vector3Builder(
            positionOffsetArray.get(0, JSONValueType.NUMBER).doubleValue(),
            positionOffsetArray.get(1, JSONValueType.NUMBER).doubleValue(),
            positionOffsetArray.get(2, JSONValueType.NUMBER).doubleValue()
        );
        final TripleAxisRotationBuilder rotationOffset = new TripleAxisRotationBuilder(
            rotationOffsetArray.get(0, JSONValueType.NUMBER).floatValue(),
            rotationOffsetArray.get(1, JSONValueType.NUMBER).floatValue(),
            rotationOffsetArray.get(2, JSONValueType.NUMBER).floatValue()
        );

        return builder()
            .font(new TupleT<>(font1, font2))
            .frameTime(frameTime)
            .maxFrames(maxFrames)
            .scale(scale)
            .positionOffset(positionOffset)
            .rotationOffset(rotationOffset);
    }
}
