package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.Combo;
import com.gmail.subnokoii78.testplugin.system.PlayerComboHandler;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiveEvent;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiverRegistry;
import com.gmail.subnokoii78.util.event.PlayerLeftClickEvent;
import com.gmail.subnokoii78.util.execute.*;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.file.json.TypedJSONArray;
import com.gmail.subnokoii78.util.eval.CalcExpEvaluator;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.shape.ParticleSpawner;
import com.gmail.subnokoii78.util.shape.VectorPrinter;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
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

public final class CustomEventListener {
    public static final CustomEventListener INSTANCE = new CustomEventListener();

    private CustomEventListener() {}

    public void onLeftClick(PlayerLeftClickEvent event) {
        // 左クリックしたプレイヤー
        final Player player = event.getPlayer();
        // プレイヤーの右手にあるアイテム
        final ItemStack itemStack = player.getEquipment().getItem(EquipmentSlot.HAND);
        // プレイヤーのコンボを管理するためのオブジェクト
        final PlayerComboHandler handler = PlayerComboHandler.getHandler(player);

        // 鉄剣じゃなかったらreturn
        if (!itemStack.getType().equals(Material.IRON_SWORD)) return;

        // 鉄剣持ってたら殴りとかブロック破壊をキャンセル
        event.cancel();

        handler.combo(Combo.KNIGHT_NORMAL_SLASH);

        handler.nextCombo();
    }

    public int onCallBoundingBox(DataPackMessageReceiveEvent event) {
        final JSONObject message = event.getMessage();

        final TypedJSONArray<Number> size = message.get("size", JSONValueType.ARRAY)
            .typed(JSONValueType.NUMBER);

        final TiltedBoundingBox box = new TiltedBoundingBox(
            size.get(0).doubleValue(),
            size.get(1).doubleValue(),
            size.get(2).doubleValue()
        );

        final float roll = message.get("roll", JSONValueType.NUMBER).floatValue();

        box.put(event.getBukkitLocation());
        box.rotation(box.rotation().roll(roll));

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
                final var spawner = new ParticleSpawner<>(Particle.WITCH, null);
                spawner.place(stack.getDimension(), hit);
                spawner.spawn();
                return Execute.SUCCESS;
            });

        return 1;
    }

    public int onCallKBVector3(DataPackMessageReceiveEvent event) {
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

        return 1;
    }

    public int onCallKBVector2(DataPackMessageReceiveEvent event) {
        final DualAxisRotationBuilder vector2 = DualAxisRotationBuilder.from(event.getBukkitLocation());
        final double strength = event.getMessage().get("strength", JSONValueType.NUMBER).doubleValue();

        event.getTargets().forEach(entity -> {
            entity.setVelocity(
                Vector3Builder
                    .from(entity.getVelocity())
                    .add(vector2.getDirection3d().length(strength))
                    .toBukkitVector()
            );
        });

        return 1;
    }

    public int onCallRotateDisplay(DataPackMessageReceiveEvent event) {
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

        return 1;
    }

    public int onCallTransfer(DataPackMessageReceiveEvent event) {
        final PaperVelocityManager.BoAServerType serverType = PaperVelocityManager.BoAServerType.valueOf(
            event.getMessage().get("server", JSONValueType.STRING)
        );

        for (final Entity target : event.getTargets()) {
            if (!(target instanceof Player player)) continue;

            TestPlugin.getPaperVelocityManager().transfer(player, serverType);
        }

        return 1;
    }

    public int onCallLogging(DataPackMessageReceiveEvent event) {
        TestPlugin.getInstance().getLogger().info(event.getMessage().get("message", JSONValueType.STRING));
        return 1;
    }

    public int onCallEvaluate(DataPackMessageReceiveEvent event) {
        final String expression = event.getMessage().get("expression", JSONValueType.STRING);
        final double value = CalcExpEvaluator.getDefaultEvaluator().evaluate(expression);
        return (int) value;
    }

    public int onCallIsEnabled(DataPackMessageReceiveEvent event) {
        return 1;
    }

    public void registerDataPackMessageIds() {
        DataPackMessageReceiverRegistry.register("spawn_bounding_box", CustomEventListener.INSTANCE::onCallBoundingBox);
        DataPackMessageReceiverRegistry.register("knockback_vec2", CustomEventListener.INSTANCE::onCallKBVector2);
        DataPackMessageReceiverRegistry.register("knockback_vec3", CustomEventListener.INSTANCE::onCallKBVector3);
        DataPackMessageReceiverRegistry.register("rotate_display", CustomEventListener.INSTANCE::onCallRotateDisplay);
        DataPackMessageReceiverRegistry.register("transfer", CustomEventListener.INSTANCE::onCallTransfer);
        DataPackMessageReceiverRegistry.register("logging", CustomEventListener.INSTANCE::onCallLogging);
        DataPackMessageReceiverRegistry.register("evaluate", CustomEventListener.INSTANCE::onCallEvaluate);
        DataPackMessageReceiverRegistry.register("is_enabled", CustomEventListener.INSTANCE::onCallIsEnabled);
    }
}
