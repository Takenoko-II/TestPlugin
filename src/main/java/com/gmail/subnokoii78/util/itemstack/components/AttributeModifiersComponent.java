package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.TypedAttributeModifier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class AttributeModifiersComponent implements TooltipShowable {
    private final ItemMeta itemMeta;

    private AttributeModifiersComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasAttributeModifiers();
    }

    @Override
    public void disable() {
        for (final TypedAttributeModifier modifier : getModifiers()) {
            removeModifier(modifier);
        }
    }

    public TypedAttributeModifier[] getModifiers() {
        final Multimap<Attribute, AttributeModifier> map = itemMeta.getAttributeModifiers();

        if (map == null) {
            return new TypedAttributeModifier[0];
        }

        return TypedAttributeModifier.from(map);
    }

    public void setModifiers(TypedAttributeModifier[] modifiers) {
        final Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        for (final TypedAttributeModifier modifier : modifiers) {
            map.put(modifier.getType(), modifier.toUnTyped());
        }

        itemMeta.setAttributeModifiers(map);
    }

    public void addModifier(TypedAttributeModifier modifier) {
        itemMeta.addAttributeModifier(modifier.getType(), modifier.toUnTyped());
    }

    public void removeModifier(TypedAttributeModifier modifier) {
        itemMeta.removeAttributeModifier(modifier.getType(), modifier.toUnTyped());
    }

    public void removeModifiers(Attribute type) {
        itemMeta.removeAttributeModifier(type);
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        else itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:attribute_modifiers";
    }
}
