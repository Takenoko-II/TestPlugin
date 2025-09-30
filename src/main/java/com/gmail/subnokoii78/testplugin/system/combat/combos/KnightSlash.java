package com.gmail.subnokoii78.testplugin.system.combat.combos;

import com.gmail.subnokoii78.testplugin.system.combat.PlayerComboHandle;
import com.gmail.subnokoii78.testplugin.system.combat.animation.AnimatorDisplayState;
import com.gmail.subnokoii78.testplugin.system.combat.animation.ItemDisplayAnimator;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.subnokoii78.tplcore.execute.EntityAnchor;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.vector.OrientedBoundingBox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public class KnightSlash extends Combo {
    private KnightSlash() {
        super("knight_slash", 4, 30, 6);
    }

    @Override
    public void onComboProgress(Player player, int currentComboCount) {
        final PlayerComboHandle handler = PlayerComboHandle.getHandle(player);
        final OrientedBoundingBox box = new OrientedBoundingBox(4, 0.5, 2);

        new Execute(new CommandSourceStack(player))
            .anchored(EntityAnchor.EYES)
            .positioned.$("^ ^ ^1")
            .run
            .onCatch((s, e) -> {
                throw (RuntimeException) e;
            })
            .callback(stack -> {
                animator.put(stack.getLocation());
                final AnimatorDisplayState state = animator.animate(currentComboCount);

                box.put(stack.getLocation());
                box.rotation(state.rotation());
                box.showOutline(Color.BLUE);

                final Set<Entity> entities = new HashSet<>(box.getCollidingEntities())
                    .stream().filter(entity -> {
                        if (entity instanceof Mob || entity instanceof Player) {
                            return !entity.equals(player);
                        }
                        else return false;
                    })
                    .collect(Collectors.toSet());

                if (entities.isEmpty()) {
                    handler.stopCombo();
                    return Execute.FAILURE;
                }
                else {
                    entities.forEach(entity -> {
                        if (entity instanceof Damageable damageable) {
                            damageable.damage(2, player);
                        }
                    });

                    return Execute.SUCCESS;
                }
            });

        player.sendMessage(Component.text("コンボ数: " + currentComboCount));
    }

    @Override
    public void onComboIsInCT(Player player) {
        player.sendMessage(Component.text("CT中").color(NamedTextColor.GRAY));
    }

    @Override
    public void onComboComplete(Player player) {
        player.sendMessage(Component.text("コンボ完成").color(NamedTextColor.GREEN));
    }

    @Override
    public void onComboStop(Player player) {
        player.sendMessage(Component.text("コンボ中断").color(NamedTextColor.RED));
    }

    public ItemDisplayAnimator animator = ItemDisplayAnimator.fromConfig(getId());

    public static final KnightSlash KNIGHT_SLASH = new KnightSlash();
}
