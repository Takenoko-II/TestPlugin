package com.gmail.subnokoii78.testplugin.system.combat;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.Job;
import com.gmail.subnokoii78.testplugin.system.combat.combos.ArcherShoot;
import com.gmail.subnokoii78.testplugin.system.items.JobItem;
import com.gmail.subnokoii78.tplcore.entity.FakeArrowLauncher;
import com.gmail.subnokoii78.tplcore.events.PlayerBowShootEvent;
import com.gmail.subnokoii78.tplcore.events.PlayerClickEvent;
import com.gmail.subnokoii78.tplcore.events.PlayerUsingItemEvent;
import com.gmail.subnokoii78.tplcore.random.RandomService;
import com.gmail.subnokoii78.tplcore.random.Xorshift32;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;

import java.text.NumberFormat;

public class CombatSystemTriggerObserver implements Listener {
    private CombatSystemTriggerObserver() {}

    public void onPlayerUsingItem(PlayerUsingItemEvent event) {
        final Player player = event.getPlayer();

        if (!(Job.hasJob(player) && Job.getJob(player) == Job.ARCHER)) return;

        if (event.getUsedTime() == 10 && event.getItem().equals(JobItem.ARCHER_BOW.get())) {
            player.playSound(
                player.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                5,
                2
            );

            final BoundingBox box = player.getBoundingBox();

            new ParticleSpawner<>(Particle.WITCH)
                .count(8)
                .delta(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()).scale(2/3d))
                .speed(2)
                .place(player.getLocation())
                .spawn();
        }
    }

    public void onPlayerBowShoot(PlayerBowShootEvent event) {
        final Player player = event.getPlayer();

        if (!Job.hasJob(player)) return;
        if (Job.getJob(player) != Job.ARCHER) return;
        if (!JobItem.ARCHER_BOW.get().equals(event.getBow())) return;

        if (event.getTicks() < 10) {
            event.cancel();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5, 1);
            player.sendMessage(Component.text(
                "0.5s以上チャージする必要があります"
            ));
        }
        else {
            event.cancel();

            final FakeArrowLauncher launcher = ArcherShoot.ARCHER_SHOOT.getLauncher(event.getPlayer());
            final FakeArrowLauncher.FakeArrow fakeArrow = launcher.launch(event.getBow(), event.getArrow());
            final PlayerComboHandle handle = PlayerComboHandle.getHandle(event.getPlayer());

            fakeArrow.onBlockHit(e -> {
                handle.stopCombo();
                e.getBlock().getWorld().playSound(
                    e.getBlock().getLocation(),
                    Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                    4f,
                    1.5f
                );
                new ParticleSpawner<>(Particle.CRIT)
                    .place(e.getFakeArrow().getEntity().getLocation())
                    .count(10)
                    .delta(new Vector3Builder(1,1,1).scale(1/2d))
                    .speed(0.5)
                    .spawn();
            });

            fakeArrow.onEntityHit(e -> {
                handle.nextCombo(e.getEntity());
            });
        }
    }

    public void onPlayerClick(PlayerClickEvent event) {
        if (!event.getClick().equals(PlayerClickEvent.Click.LEFT)) return;
        if (!event.hasItem()) return;

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (!Job.hasJob(player)) return;
        final PlayerComboHandle handle = PlayerComboHandle.getHandle(player);

        if (Job.getJob(player) == Job.KNIGHT) {
            if (!JobItem.KNIGHT_BLADE.get().equals(itemStack)) return;
            event.cancel();
            handle.nextCombo();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        final Entity hurtEntity = event.getEntity();
        final BoundingBox box = hurtEntity.getBoundingBox();

        final TextDisplay indicator = hurtEntity.getWorld().spawn(
            Vector3Builder.from(hurtEntity)
                .add(new Vector3Builder(0, box.getHeight(), 0))
                .withWorld(hurtEntity.getWorld()),
            TextDisplay.class,
            entity -> {
                entity.addScoreboardTag(TestPlugin.INTERNAL_ENTITY_TAG);
                entity.text(
                    Component.text("-" + event.getFinalDamage())
                        .color(NamedTextColor.RED)
                );
                entity.setBillboard(Display.Billboard.CENTER);
                final Transformation transformation = entity.getTransformation();
                transformation.getScale().set(1.5, 1.5, 1.5);
                entity.setTransformation(transformation);
            }
        );

        final RandomService randomService = new RandomService(Xorshift32.random());
        final Vector3Builder dir = randomService.rotation2().getDirection3d();
        dir.y(0.33);

        final GameTickScheduler tper = new GameTickScheduler(self -> {
            if (indicator.isValid()) {
                indicator.teleport(
                    Vector3Builder.from(indicator).add(dir.copy().scale(0.075))
                        .withWorld(indicator.getWorld())
                );
            }
            else self.clear();
        });
        tper.runInterval();

        new GameTickScheduler(() -> {
            if (indicator.isValid()) {
                tper.clear();
                indicator.remove();
            }
        }).runTimeout(20);
    }

    public static final CombatSystemTriggerObserver INSTANCE = new CombatSystemTriggerObserver();
}
