package com.gmail.subnokoii78.util.itemstack;

import com.google.common.collect.Multimap;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
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

    public double amount() {
        return modifier.getAmount();
    }

    public TypedAttributeModifier amount(double value) {
        modifier = new AttributeModifier(
            modifier.getKey(),
            value,
            modifier.getOperation(),
            modifier.getSlotGroup()
        );

        return this;
    }

    public AttributeModifier.Operation operation() {
        return modifier.getOperation();
    }

    public TypedAttributeModifier operation(@NotNull AttributeModifier.Operation operation) {
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

    public EquipmentSlotGroup activeSlots() {
        return modifier.getSlotGroup();
    }

    public TypedAttributeModifier activeSlots(@NotNull EquipmentSlotGroup slotGroup) {
        modifier = new AttributeModifier(
            modifier.getKey(),
            modifier.getAmount(),
            modifier.getOperation(),
            slotGroup
        );

        return this;
    }

    public AttributeModifier toBukkit() {
        return modifier;
    }

    public static TypedAttributeModifier fromBukkit(AttributeModifier modifier) {
        return new TypedAttributeModifier(Attribute.valueOf(modifier.getName()))
            .amount(modifier.getAmount())
            .operation(modifier.getOperation())
            .activeSlots(modifier.getSlotGroup());
    }

    public static TypedAttributeModifier[] fromBukkit(Multimap<Attribute, AttributeModifier> modifiers) {
        return modifiers.entries()
            .stream()
            .map(Map.Entry::getValue)
            .map(TypedAttributeModifier::fromBukkit)
            .toArray(TypedAttributeModifier[]::new);
    }
}
