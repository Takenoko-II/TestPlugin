package com.gmail.subnokoii78.util.itemstack.components;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public final class AttributeModifiersComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private AttributeModifiersComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return itemMeta.hasAttributeModifiers();
    }

    @Override
    public void disable() {
        for (final Modifier modifier : getModifiers()) {
            removeModifier(modifier);
        }
    }

    public Modifier[] getModifiers() {
        final Multimap<Attribute, AttributeModifier> map = itemMeta.getAttributeModifiers();

        if (map == null) {
            return new Modifier[0];
        }

        return Modifier.from(map);
    }

    public void setModifiers(Modifier[] modifiers) {
        final Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        for (final Modifier modifier : modifiers) {
            map.put(modifier.getType(), modifier.modifier);
        }

        itemMeta.setAttributeModifiers(map);
    }

    public void addModifier(Modifier modifier) {
        itemMeta.addAttributeModifier(modifier.getType(), modifier.modifier);
    }

    public void removeModifier(Modifier modifier) {
        itemMeta.removeAttributeModifier(modifier.getType(), modifier.modifier);
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

    public static final String COMPONENT_ID = "minecraft:attribute_modifiers";

    public static final class Modifier {
        private AttributeModifier modifier;

        public Modifier(Attribute type) {
            this.modifier = new AttributeModifier(UUID.randomUUID(), type.name(), type.ordinal(), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        }

        public double getValue() {
            return modifier.getAmount();
        }

        public Modifier setValue(double value) {
            modifier = new AttributeModifier(modifier.getUniqueId(), modifier.getName(), value, modifier.getOperation(), modifier.getSlot());

            return this;
        }

        public AttributeModifier.Operation getOperation() {
            return modifier.getOperation();
        }

        public Modifier setOperation(AttributeModifier.Operation operation) {
            modifier = new AttributeModifier(modifier.getUniqueId(), modifier.getName(), modifier.getAmount(), operation, modifier.getSlot());

            return this;
        }

        public Attribute getType() {
            return Attribute.valueOf(modifier.getName());
        }

        public EquipmentSlot getSlot() {
            return modifier.getSlot();
        }

        public Modifier setSlot(EquipmentSlot slot) {
            modifier = new AttributeModifier(modifier.getUniqueId(), modifier.getName(), modifier.getAmount(), modifier.getOperation(), slot);

            return this;
        }

        public static Modifier from(AttributeModifier modifier) {
            return new Modifier(Attribute.valueOf(modifier.getName()))
            .setValue(modifier.getAmount())
            .setOperation(modifier.getOperation())
            .setSlot(modifier.getSlot());
        }

        public static Modifier[] from(Multimap<Attribute, AttributeModifier> modifiers) {
            return modifiers.entries().stream().map(Map.Entry::getValue).map(Modifier::from).toArray(Modifier[]::new);
        }
    }
}
