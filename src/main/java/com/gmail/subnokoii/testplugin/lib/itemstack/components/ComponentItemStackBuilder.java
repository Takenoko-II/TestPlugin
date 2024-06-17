package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * コンポーネント操作によってアイテムデータを作成していくスタイルのItemStackBuilder
 * コンポーネントによって管理されていないデータは操作できません
 */
public final class ComponentItemStackBuilder {
    private final ItemStack itemStack;

    private final ItemMeta itemMeta;

    /**
     * アイテムタイプから新しくアイテムを作成します。
     * @param type アイテムタイプ
     * @throws IllegalArgumentException ItemMetaが取得できないアイテムタイプが渡されたとき
     */
    public ComponentItemStackBuilder(Material type) {
        itemStack = new ItemStack(type);
        itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            throw new IllegalArgumentException("このアイテムタイプはItemMetaを持っていません");
        }
    }

    /**
     * アイテムスタックから新しくアイテムを作成します。
     * @param itemStack アイテムスタック
     * @throws IllegalArgumentException 引数がnullのとき
     */
    public ComponentItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            throw new IllegalArgumentException("このアイテムタイプはItemMetaを持っていません");
        }
    }

    /**
     * このオブジェクトを通常のItemStackBuilderに変換します。
     * @return ItemStackBuilder
     */
    public ItemStackBuilder toItemStackBuilder() {
        itemStack.setItemMeta(itemMeta);
        return ItemStackBuilder.from(itemStack.clone());
    }

    /**
     * コンポーネントタイプを基にコンポーネントを取得します。
     * @param type コンポーネントタイプ
     * @return 対応するコンポーネント
     */
    public <T extends ItemStackComponent> T get(ItemStackComponent.ComponentType<T> type) {
        return type.getComponent(itemMeta);
    }

    /**
     * 持っている全種類のコンポーネントを取得します。
     * @return コンポーネントの配列
     */
    public ItemStackComponent[] getAll() {
        final List<ItemStackComponent> components = new ArrayList<>();

        for (final ItemStackComponent.ComponentType<?> type : ItemStackComponent.ComponentType.values()) {
            final ItemStackComponent component = type.getComponent(itemMeta);

            if (component.getEnabled()) {
                components.add(component);
            }
        }

        return components.toArray(ItemStackComponent[]::new);
    }

    /**
     * コンポーネントを何か一つでも持っているかを返します。
     * @return コンポーネントを一つでも持っていれば真
     */
    public boolean hasAny() {
        for (final ItemStackComponent.ComponentType<?> type : ItemStackComponent.ComponentType.values()) {
            if (type.getComponent(itemMeta).getEnabled()) return true;
        }

        return false;
    }

    /**
     * 全コンポーネントを削除します。
     */
    public void deleteAll() {
        for (final ItemStackComponent.ComponentType<?> type : ItemStackComponent.ComponentType.values()) {
            type.getComponent(itemMeta).disable();
        }
    }

    public UnbreakableComponent unbreakable() {
        return get(ItemStackComponent.ComponentType.UNBREAKABLE);
    }

    public MaxStackSizeComponent maxStackSize() {
        return get(ItemStackComponent.ComponentType.MAX_STACK_SIZE);
    }

    public EnchantmentGlintOverrideComponent enchantmentGlintOverride() {
        return get(ItemStackComponent.ComponentType.ENCHANT_GLINT_OVERRIDE);
    }

    public CustomNameComponent customName() {
        return get(ItemStackComponent.ComponentType.CUSTOM_NAME);
    }

    public LoreComponent lore() {
        return get(ItemStackComponent.ComponentType.LORE);
    }

    public AttributeModifiersComponent attributeModifiers() {
        return get(ItemStackComponent.ComponentType.ATTRIBUTE_MODIFIERS);
    }

    public RepairCostComponent repairCost() {
        return get(ItemStackComponent.ComponentType.REPAIR_COST);
    }

    public ItemNameComponent itemName() {
        return get(ItemStackComponent.ComponentType.ITEM_NAME);
    }

    public MaxDamageComponent maxDamage() {
        return get(ItemStackComponent.ComponentType.MAX_DAMAGE);
    }

    public EnchantmentsComponent enchantments() {
        return get(ItemStackComponent.ComponentType.ENCHANTMENTS);
    }

    public StoredEnchantmentsComponent storedEnchantments() {
        return get(ItemStackComponent.ComponentType.STORED_ENCHANTMENTS);
    }

    public PotionContentsComponent potionContents() {
        return get(ItemStackComponent.ComponentType.POTION_CONTENTS);
    }

    public CustomModelDataComponent customModelData() {
        return get(ItemStackComponent.ComponentType.CUSTOM_MODEL_DATA);
    }

    public TrimComponent trim() {
        return get(ItemStackComponent.ComponentType.TRIM);
    }

    public DyedColorComponent dyedColor() {
        return get(ItemStackComponent.ComponentType.DYED_COLOR);
    }

    public ProfileComponent profile() {
        return get(ItemStackComponent.ComponentType.PROFILE);
    }

    public RecipesComponent recipes() {
        return get(ItemStackComponent.ComponentType.RECIPES);
    }
}
