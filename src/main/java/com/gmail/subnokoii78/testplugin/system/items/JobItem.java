package com.gmail.subnokoii78.testplugin.system.items;

import com.gmail.subnokoii78.testplugin.system.Job;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum JobItem {
    KNIGHT_BLADE {
        @Override
        public ItemStack get() {
            return new ItemStackBuilder(Material.IRON_SWORD)
                .itemName(
                    Component.text("Knight Blade")
                        .color(NamedTextColor.GOLD)
                )
                .customData(
                    MojangsonPath.of("tpl.job"),
                    Job.KNIGHT.getId()
                )
                .build();
        }
    },

    ARCHER_BOW {
        @Override
        public ItemStack get() {
            return new ItemStackBuilder(Material.BOW)
                .itemName(
                    Component.text("Archer Bow")
                        .color(NamedTextColor.GOLD)
                )
                .customData(
                    MojangsonPath.of("tpl.job"),
                    Job.ARCHER.getId()
                )
                .build();
        }
    };

    JobItem() {

    }

    public ItemStack get() {
        throw new IllegalStateException();
    }
}
