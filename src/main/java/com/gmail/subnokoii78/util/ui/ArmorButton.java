package com.gmail.subnokoii78.util.ui;

import com.destroystokyo.paper.MaterialSetTag;
import com.gmail.subnokoii78.util.itemstack.TypedAttributeModifier;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public class ArmorButton extends ItemButton {
    public ArmorButton(@NotNull Material material) {
        super(material);

        if (!MaterialSetTag.ITEMS_TRIMMABLE_ARMOR.isTagged(material)) {
            throw new IllegalArgumentException();
        }
    }

    public @NotNull ArmorButton trim(@NotNull TrimMaterial material, @NotNull TrimPattern pattern) {
        itemStackBuilder.trim().setTrim(new ArmorTrim(material, pattern));
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.trim().setShowInTooltip(false);
        itemStackBuilder.attributeModifiers().addModifier(new TypedAttributeModifier(Attribute.GENERIC_ARMOR));
        itemStackBuilder.attributeModifiers().setShowInTooltip(false);
        return super.build();
    }
}
