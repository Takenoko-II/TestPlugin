package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.system.combat.PlayerComboHandle;
import com.gmail.subnokoii78.testplugin.system.combat.combos.KnightSlash;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.DatapackMessageReceiveEvent;
import com.gmail.subnokoii78.tplcore.events.PlayerClickEvent;
import com.gmail.subnokoii78.tplcore.execute.EntityAnchor;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.tplcore.shape.VectorPrinter;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.OrientedBoundingBox;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonDouble;
import com.gmail.takenokoii78.mojangson.values.MojangsonFloat;
import com.gmail.takenokoii78.mojangson.values.TypedMojangsonList;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.Set;
import java.util.function.Function;

public final class CustomEventListener {
    public static final CustomEventListener INSTANCE = new CustomEventListener();

    private CustomEventListener() {}

    public int onCallBoundingBox(DatapackMessageReceiveEvent event) {
        final MojangsonCompound input = event.getInput();

        final TypedMojangsonList<MojangsonFloat> size = input.get("size", MojangsonValueTypes.LIST)
            .typed(MojangsonValueTypes.FLOAT);

        final OrientedBoundingBox box = new OrientedBoundingBox(
            size.get(0).doubleValue(),
            size.get(1).doubleValue(),
            size.get(2).doubleValue()
        );

        final float roll = input.get("roll", MojangsonValueTypes.FLOAT).floatValue();

        box.put(event.getContext().getLocation());
        box.rotation(box.rotation().roll(roll));

        final Set<Entity> entities = box.getCollidingEntities();

        entities.forEach(entity -> {
            entity.addScoreboardTag("plugin_api.box_intersection");
        });

        if (input.get("show_outline", MojangsonValueTypes.BYTE).getAsBooleanValueOrNull() == Boolean.TRUE) {
            if (entities.isEmpty()) {
                box.showOutline();
            }
            else {
                box.showOutline(Color.RED);
            }
        }

        final var execute = new Execute();
        execute
            .as(EntitySelector.E.arg(SelectorArgument.TAG, "Test"))
            .at(EntitySelector.S)
            .anchored(EntityAnchor.EYES)
            .positioned.$("^ ^ ^")
            .run.callback(stack -> {
                final var printer = new VectorPrinter(stack.getLocation());
                final var from = stack.getPosition();
                final var delta = stack.getRotation().getDirection3d().length(15);
                printer.print(delta, Color.BLUE);
                final Vector3Builder hit = box.rayCast(from, stack.getPosition().add(delta));
                if (hit == null) return Execute.FAILURE;
                final var spawner = new ParticleSpawner<>(Particle.WITCH);
                spawner.place(stack.getDimension(), hit);
                spawner.spawn();
                return Execute.SUCCESS;
            });

        return 1;
    }

    public int onCallKBVector3(DatapackMessageReceiveEvent event) {
        final TypedMojangsonList<MojangsonDouble> array = event.getInput()
            .get("vector3", MojangsonValueTypes.LIST)
            .typed(MojangsonValueTypes.DOUBLE);

        final Vector3Builder vector3 = new Vector3Builder(
            array.get(0).doubleValue(),
            array.get(1).doubleValue(),
            array.get(2).doubleValue()
        );

        event.getTargets().forEach(entity -> {
            entity.setVelocity(
                Vector3Builder
                    .from(entity.getVelocity())
                    .add(vector3)
                    .toBukkitVector()
            );
        });

        return 1;
    }

    public int onCallKBVector2(DatapackMessageReceiveEvent event) {
        final DualAxisRotationBuilder vector2 = event.getContext().getRotation();
        final double strength = event.getInput().get("strength", MojangsonValueTypes.DOUBLE).doubleValue();

        if (!event.getContext().hasExecutor()) {
            return 0;
        }

        final Entity executor = event.getContext().getExecutor();
        executor.setVelocity(
            Vector3Builder
                .from(executor.getVelocity())
                .add(vector2.getDirection3d().length(strength))
                .toBukkitVector()
        );

        return 1;
    }

    public int onCallRotateDisplay(DatapackMessageReceiveEvent event) {
        final TypedMojangsonList<MojangsonFloat> array = event.getInput()
            .get("rotation", MojangsonValueTypes.LIST)
            .typed(MojangsonValueTypes.FLOAT);

        final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder(
            array.get(0).floatValue(),
            array.get(1).floatValue(),
            array.get(2).floatValue()
        );

        for (final Entity target : event.getTargets()) {
            if (!(target instanceof Display display)) continue;

            final Transformation transformation = display.getTransformation();
            transformation.getLeftRotation().set(rotation.getQuaternion4f());
            display.setTransformation(transformation);
        }

        return 1;
    }

    public int onCallTransfer(DatapackMessageReceiveEvent event) {
        final PaperVelocityManager.BoAServer serverType = PaperVelocityManager.BoAServer.valueOf(
            event.getInput().get("server", MojangsonValueTypes.STRING).getValue()
        );

        for (final Entity target : event.getTargets()) {
            if (!(target instanceof Player player)) continue;

            TPLCore.paperVelocityManager.transfer(player, serverType);
        }

        return 1;
    }

    public int onCallIsEnabled(DatapackMessageReceiveEvent event) {
        return 1;
    }

    public void onDatapackMessageReceive(DatapackMessageReceiveEvent event) {
        final Function<DatapackMessageReceiveEvent, Integer> listener = switch (event.getId()) {
            case "spawn_bounding_box" -> CustomEventListener.INSTANCE::onCallBoundingBox;
            case "knockback_vec2" -> CustomEventListener.INSTANCE::onCallKBVector2;
            case "knockback_vec3" -> CustomEventListener.INSTANCE::onCallKBVector3;
            case "rotate_display" -> CustomEventListener.INSTANCE::onCallRotateDisplay;
            case "transfer" -> CustomEventListener.INSTANCE::onCallTransfer;
            case "is_enabled" -> CustomEventListener.INSTANCE::onCallIsEnabled;
            default -> $ -> 0;
        };
        event.setReturnValue(listener.apply(event));
    }
}
