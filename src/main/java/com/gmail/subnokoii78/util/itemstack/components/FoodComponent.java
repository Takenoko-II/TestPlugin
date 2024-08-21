package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.PotionContent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent.FoodEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class FoodComponent extends ItemStackComponent {
    private FoodComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasFood();
    }

    @Override
    public void disable() {
        itemMeta.setFood(null);
    }

    public int nutrition() {
        return itemMeta.getFood().getNutrition();
    }

    public FoodComponent nutrition(int value) {
        itemMeta.getFood().setNutrition(value);
        return this;
    }

    public float saturation() {
        return itemMeta.getFood().getSaturation();
    }

    public FoodComponent saturation(float value) {
        itemMeta.getFood().setSaturation(value);
        return this;
    }

    public int eatTicks() {
        return (int) Math.floor(itemMeta.getFood().getEatSeconds() * 20);
    }

    public FoodComponent eatTicks(int ticks) {
        itemMeta.getFood().setEatSeconds(((float) ticks) / 20f);
        return this;
    }

    public boolean canAlwaysEat() {
        return itemMeta.getFood().canAlwaysEat();
    }

    public FoodComponent canAlwaysEat(boolean flag) {
        itemMeta.getFood().setCanAlwaysEat(flag);
        return this;
    }

    public Map<PotionContent, Float> effects() {
        final Map<PotionContent, Float> map = new HashMap<>();

        itemMeta.getFood()
            .getEffects()
            .stream()
            .forEach(foodEffect -> {
                final PotionEffect effect = foodEffect.getEffect();
                map.put(
                    PotionContent.fromBukkit(effect),
                    foodEffect.getProbability()
                );
            });

        return map;
    }

    public FoodComponent addEffect(@NotNull PotionContent effect, float probability) {
        itemMeta.getFood().addEffect(effect.toBukkit(), probability);
        return this;
    }

    public FoodComponent removeEffect(@NotNull PotionEffectType type) {
        final List<FoodEffect> effects = new ArrayList<>(itemMeta.getFood().getEffects());
        itemMeta.getFood().setEffects(
            effects.stream()
                .filter(effect -> !effect.getEffect().getType().equals(type))
                .toList()
        );
        return this;
    }

    public FoodComponent effects(@NotNull Map<PotionContent, Float> map) {
        map.keySet().forEach(content -> itemMeta.getFood().addEffect(content.toBukkit(), map.get(content)));
        return this;
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:food";
    }
}
