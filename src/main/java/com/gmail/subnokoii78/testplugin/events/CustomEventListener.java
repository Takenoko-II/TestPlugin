package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.PlayerComboHandler;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiveEvent;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiverRegistry;
import com.gmail.subnokoii78.util.event.PlayerLeftClickEvent;
import com.gmail.subnokoii78.util.execute.EntityAnchorType;
import com.gmail.subnokoii78.util.execute.Execute;
import com.gmail.subnokoii78.util.execute.SourceOrigin;
import com.gmail.subnokoii78.util.execute.SourceStack;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.file.json.TypedJSONArray;
import com.gmail.subnokoii78.util.other.CalcExpEvaluator;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.HashSet;
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

        // 横4, 縦0.5, 奥行き2のボックス
        final TiltedBoundingBox box = new TiltedBoundingBox(4, 0.5, 2);

        new Execute(new SourceStack(SourceOrigin.of(player)))
            .anchored(EntityAnchorType.EYES)
            .positioned.$("^ ^ ^1")
            .run.callback(stack -> {
                // 適切な位置にボックス設置
                box.put(stack.getAsBukkitLocation());
                return Execute.SUCCESS;
            });

        // 現在の段数に応じて角度決定
        box.rotation(box.rotation().roll(switch (handler.getCurrentComboCount()) {
            case 0 -> -60f;
            case 1 -> 30f;
            case 2 -> 90f;
            default -> throw new IllegalStateException("NEVER HAPPENS");
        }));

        // 外枠表示
        box.showOutline(Color.RED);

        // ボックスに衝突しているエンティティを取得
        final Set<Entity> entities = new HashSet<>(box.getCollidingEntities());
        // プレイヤー本人は除外
        entities.remove(player);

        if (entities.isEmpty()) {
            // 衝突してるエンティティいなければコンボ止める
            handler.stopCombo();
        }
        else {
            // コンボを次に進める
            int comboCount = handler.nextCombo();

            // CT中の場合-1が返るので条件式は>0
            if (comboCount > 0) {
                // CT中じゃなければダメージとメッセージ
                entities.forEach(entity -> {
                    if (entity instanceof Damageable damageable) {
                        damageable.damage(2, player);
                    }
                });

                player.sendMessage(Component.text("段数: " + comboCount));
            }
            else {
                // CT中のとき
                player.sendMessage(Component.text("CT中").color(NamedTextColor.GRAY));
            }
        }
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
