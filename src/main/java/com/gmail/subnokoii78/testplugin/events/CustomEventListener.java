package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.PlayerComboHandler;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiveEvent;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiverRegistry;
import com.gmail.subnokoii78.util.event.PlayerLeftClickEvent;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.file.json.TypedJSONArray;
import com.gmail.subnokoii78.util.other.CalcExpEvaluator;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.Set;

public final class CustomEventListener {
    public static final CustomEventListener INSTANCE = new CustomEventListener();

    private CustomEventListener() {}

    public void onLeftClick(PlayerLeftClickEvent event) {
        final PlayerComboHandler handler = PlayerComboHandler.getHandler(event.getPlayer());

        final ItemStack itemStack = event.getPlayer().getEquipment().getItem(EquipmentSlot.HAND);

        if (!itemStack.getType().equals(Material.IRON_SWORD)) return;

        if (PlayerLeftClickEvent.Action.ENTITY_HIT.equals(event.getAction())) {
            final Integer currentCount = handler.nextCombo();
            if (currentCount == null) {
                event.getPlayer().sendMessage(Component.text("CT中").color(NamedTextColor.GRAY));
                event.cancel();
            }
            else event.getPlayer().sendMessage(Component.text("段数: " + currentCount));
        }
        else {
            handler.stopCombo();
        }
    }

    public void onCallBoundingBox(DataPackMessageReceiveEvent event) {
        final Entity sender = event.getMessenger();
        final JSONObject message = event.getMessage();

        final TypedJSONArray<Number> size = message.get("size", JSONValueType.ARRAY)
            .typed(JSONValueType.NUMBER);

        final TiltedBoundingBox box = new TiltedBoundingBox(
            size.get(0).doubleValue(),
            size.get(1).doubleValue(),
            size.get(2).doubleValue()
        );

        final float roll = message.get("roll", JSONValueType.NUMBER).floatValue();

        box.put(sender.getLocation());
        box.rotation(box.rotation().roll(roll));

        sender.remove();

        final Set<Entity> entities = box.getCollidingEntities();

        entities.forEach(entity -> {
            entity.addScoreboardTag("plugin_api.box_intersection");
        });

        if (event.getMessage().get("show_outline", JSONValueType.NUMBER).byteValue() > 0) {
            if (entities.isEmpty()) {
                box.showOutline();
            }
            else {
                box.showOutline(Color.RED);
            }
        }
    }

    public void onCallKBVector3(DataPackMessageReceiveEvent event) {
        final TypedJSONArray<Number> array = event.getMessage()
            .get("vector3", JSONValueType.ARRAY)
            .typed(JSONValueType.NUMBER);

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
    }

    public void onCallKBVector2(DataPackMessageReceiveEvent event) {
        final DualAxisRotationBuilder vector2 = DualAxisRotationBuilder.from(event.getMessenger());
        final double strength = event.getMessage().get("strength", JSONValueType.NUMBER).doubleValue();

        event.getTargets().forEach(entity -> {
            entity.setVelocity(
                Vector3Builder
                    .from(entity.getVelocity())
                    .add(vector2.getDirection3d().length(strength))
                    .toBukkitVector()
            );
        });
    }

    public void onCallRotateDisplay(DataPackMessageReceiveEvent event) {
        final TypedJSONArray<Number> array = event.getMessage()
            .get("rotation", JSONValueType.ARRAY)
            .typed(JSONValueType.NUMBER);

        final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder(
            array.get(0).floatValue(),
            array.get(1).floatValue(),
            array.get(2).floatValue()
        );

        for (final Entity target : event.getTargets()) {
            if (!(target instanceof Display display)) continue;

            final Transformation transformation = display.getTransformation();
            transformation.getLeftRotation().set(rotation.getQuaternion4d());
            display.setTransformation(transformation);
        }
    }

    public void onCallTransfer(DataPackMessageReceiveEvent event) {
        final PaperVelocityManager.BoAServerType serverType = PaperVelocityManager.BoAServerType.valueOf(
            event.getMessage().get("server", JSONValueType.STRING)
        );

        for (final Entity target : event.getTargets()) {
            if (!(target instanceof Player player)) continue;

            TestPlugin.getPaperVelocityManager().transfer(player, serverType);
        }
    }

    public void onCallLogging(DataPackMessageReceiveEvent event) {
        TestPlugin.getInstance().getLogger().info(event.getMessage().get("message", JSONValueType.STRING));
    }

    public void onCallEvaluate(DataPackMessageReceiveEvent event) {
        final String expression = event.getMessage().get("expression", JSONValueType.STRING);
        final String objective = event.getMessage().get("objective", JSONValueType.STRING);
        final double value = CalcExpEvaluator.getDefaultEvaluator().evaluate(expression);
        ScoreboardUtils.getOrCreateObjective(objective).setScore(event.getMessenger(), (int) value);
    }

    public void onCallIsEnabled(DataPackMessageReceiveEvent event) {
        final String objective = event.getMessage().get("objective", JSONValueType.STRING);
        ScoreboardUtils.getOrCreateObjective(objective).setScore(event.getMessenger(), 1);
    }

    public void registerDataPackMessageIds() {
        DataPackMessageReceiverRegistry.register("spawn_bounding_box", CustomEventListener.INSTANCE::onCallBoundingBox);
        DataPackMessageReceiverRegistry.register("knockback_vec2", CustomEventListener.INSTANCE::onCallKBVector2);
        DataPackMessageReceiverRegistry.register("knockback_vec3", CustomEventListener.INSTANCE::onCallKBVector3);
        DataPackMessageReceiverRegistry.register("rotate_display", CustomEventListener.INSTANCE::onCallRotateDisplay);
        DataPackMessageReceiverRegistry.register("transfer", CustomEventListener.INSTANCE::onCallTransfer);
        DataPackMessageReceiverRegistry.register("logging", CustomEventListener.INSTANCE::onCallLogging);
        DataPackMessageReceiverRegistry.register("evaluate", CustomEventListener.INSTANCE::onCallEvaluate);
        DataPackMessageReceiverRegistry.register("isEnabled", CustomEventListener.INSTANCE::onCallIsEnabled);
    }
}
