package com.gmail.subnokoii78.testplugin.util.itemstack.components;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class RecipesComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private RecipesComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return hasRecipes();
    }

    public boolean hasRecipes() {
        if (itemMeta instanceof KnowledgeBookMeta) {
            return ((KnowledgeBookMeta) itemMeta).hasRecipes();
        }
        else return false;
    }

    public boolean hasRecipe(String recipe) {
        return List.of(getRecipe()).contains(recipe);
    }

    public String[] getRecipe() {
        if (itemMeta instanceof KnowledgeBookMeta) {
            return ((KnowledgeBookMeta) itemMeta).getRecipes().stream().map(NamespacedKey::asString).toArray(String[]::new);
        }
        else return new String[0];
    }

    public void setRecipes(String... recipes) {
        final NamespacedKey[] array = Arrays.stream(recipes).map(NamespacedKey::fromString).filter(Objects::nonNull).toArray(NamespacedKey[]::new);

        if (itemMeta instanceof KnowledgeBookMeta) {
            ((KnowledgeBookMeta) itemMeta).setRecipes(List.of(array));
        }
    }

    public void addRecipes(String recipe) {
        final NamespacedKey key = NamespacedKey.fromString(recipe);

        if (key == null) return;

        if (itemMeta instanceof KnowledgeBookMeta) {
            ((KnowledgeBookMeta) itemMeta).addRecipe(key);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof KnowledgeBookMeta) {
            ((KnowledgeBookMeta) itemMeta).setRecipes(List.of());
        }
    }

    @Override
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:recipes";
}
