package com.gmail.subnokoii78.util.itemstack;

import com.google.common.collect.Multimap;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class TypedAttributeModifier {
    private AttributeModifier modifier;

    private final Attribute type;

    public TypedAttributeModifier(@NotNull Attribute type) {
        this.type = type;
        this.modifier = new AttributeModifier(
            NamespacedKey.minecraft(UUID.randomUUID().toString()),
            type.ordinal(),
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.MAINHAND
        );
    }

    public double getValue() {
        return modifier.getAmount();
    }

    public TypedAttributeModifier setValue(double value) {
        modifier = new AttributeModifier(
            modifier.getKey(),
            value,
            modifier.getOperation(),
            modifier.getSlotGroup()
        );

        return this;
    }

    public AttributeModifier.Operation getOperation() {
        return modifier.getOperation();
    }

    public TypedAttributeModifier setOperation(@NotNull AttributeModifier.Operation operation) {
        modifier = new AttributeModifier(
            modifier.getKey(),
            modifier.getAmount(),
            operation,
            modifier.getSlotGroup()
        );

        return this;
    }

    public Attribute getType() {
        return type;
    }

    public EquipmentSlotGroup getSlotGroup() {
        return modifier.getSlotGroup();
    }

    public TypedAttributeModifier setSlotGroup(@NotNull EquipmentSlotGroup slotGroup) {
        modifier = new AttributeModifier(
            modifier.getKey(),
            modifier.getAmount(),
            modifier.getOperation(),
            slotGroup
        );

        return this;
    }

    public AttributeModifier toUnTyped() {
        return modifier;
    }

    public static TypedAttributeModifier from(AttributeModifier modifier) {
        return new TypedAttributeModifier(Attribute.valueOf(modifier.getName()))
            .setValue(modifier.getAmount())
            .setOperation(modifier.getOperation())
            .setSlotGroup(modifier.getSlotGroup());
    }

    public static TypedAttributeModifier[] from(Multimap<Attribute, AttributeModifier> modifiers) {
        return modifiers.entries()
            .stream()
            .map(Map.Entry::getValue)
            .map(TypedAttributeModifier::from)
            .toArray(TypedAttributeModifier[]::new);
    }
}
