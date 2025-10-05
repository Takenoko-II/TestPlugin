package com.gmail.subnokoii78.testplugin.system;

import com.gmail.subnokoii78.testplugin.system.combat.PlayerComboHandle;
import com.gmail.subnokoii78.testplugin.system.combat.combos.ArcherShoot;
import com.gmail.subnokoii78.testplugin.system.combat.combos.Combo;
import com.gmail.subnokoii78.testplugin.system.combat.combos.KnightSlash;
import com.gmail.subnokoii78.testplugin.system.items.JobItem;
import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum Job implements CommandArgumentableEnumeration {
    KNIGHT("knight", KnightSlash.KNIGHT_SLASH, JobItem.KNIGHT_BLADE),

    ARCHER("archer", ArcherShoot.ARCHER_SHOOT, JobItem.ARCHER_BOW);

    final String id;

    final Combo normalCombo;

    final JobItem jobItem;

    private static final Map<Player, Job> map = new HashMap<>();

    Job(String id, Combo normalCombo, JobItem jobItem) {
        this.id = id;
        this.normalCombo = normalCombo;
        this.jobItem = jobItem;
    }

    public String getId() {
        return id;
    }

    public Combo getNormalCombo() {
        return normalCombo;
    }

    public static boolean hasJob(Player player) {
        return map.containsKey(player);
    }

    public static Job getJob(Player player) {
        if (map.containsKey(player)) {
            return map.get(player);
        }
        else {
            throw new IllegalStateException("プレイヤー '" + player.getName() + "' は職業を持っていません");
        }
    }

    public static void setJob(Player player, Job job) {
        takeJob(player);
        map.put(player, job);
        PlayerComboHandle.getHandle(player).setCombo(job.normalCombo);
        player.getInventory().addItem(job.jobItem.get());
    }

    public static void takeJob(Player player) {
        map.remove(player);
        for (JobItem value : JobItem.values()) {
            player.getInventory().remove(value.get());
        }
    }
}
