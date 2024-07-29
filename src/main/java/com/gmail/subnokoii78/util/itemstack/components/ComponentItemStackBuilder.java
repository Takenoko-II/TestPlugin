package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.ItemStackBuilder;
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
    public <T extends ItemStackComponent> T get(ItemStackComponentType<T> type) {
        return type.getComponentOf(itemMeta);
    }

    /**
     * 持っている全種類のコンポーネントを取得します。
     * @return コンポーネントの配列
     */
    public ItemStackComponent[] getAll() {
        final List<ItemStackComponent> components = new ArrayList<>();

        for (final ItemStackComponentType<?> type : ItemStackComponentType.values()) {
            final ItemStackComponent component = type.getComponentOf(itemMeta);

            if (component.isEnabled()) {
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
        for (final ItemStackComponentType<?> type : ItemStackComponentType.values()) {
            if (type.getComponentOf(itemMeta).isEnabled()) return true;
        }

        return false;
    }

    /**
     * 全コンポーネントを削除します。
     */
    public void deleteAll() {
        for (final ItemStackComponentType<?> type : ItemStackComponentType.values()) {
            type.getComponentOf(itemMeta).disable();
        }
    }

    public UnbreakableComponent unbreakable() {
        return get(ItemStackComponentType.UNBREAKABLE);
    }

    public MaxStackSizeComponent maxStackSize() {
        return get(ItemStackComponentType.MAX_STACK_SIZE);
    }

    public EnchantmentGlintOverrideComponent enchantmentGlintOverride() {
        return get(ItemStackComponentType.ENCHANT_GLINT_OVERRIDE);
    }

    public CustomNameComponent customName() {
        return get(ItemStackComponentType.CUSTOM_NAME);
    }

    public LoreComponent lore() {
        return get(ItemStackComponentType.LORE);
    }

    public AttributeModifiersComponent attributeModifiers() {
        return get(ItemStackComponentType.ATTRIBUTE_MODIFIERS);
    }

    public RepairCostComponent repairCost() {
        return get(ItemStackComponentType.REPAIR_COST);
    }

    public ItemNameComponent itemName() {
        return get(ItemStackComponentType.ITEM_NAME);
    }

    public MaxDamageComponent maxDamage() {
        return get(ItemStackComponentType.MAX_DAMAGE);
    }

    public EnchantmentsComponent enchantments() {
        return get(ItemStackComponentType.ENCHANTMENTS);
    }

    public StoredEnchantmentsComponent storedEnchantments() {
        return get(ItemStackComponentType.STORED_ENCHANTMENTS);
    }

    public PotionContentsComponent potionContents() {
        return get(ItemStackComponentType.POTION_CONTENTS);
    }

    public CustomModelDataComponent customModelData() {
        return get(ItemStackComponentType.CUSTOM_MODEL_DATA);
    }

    public TrimComponent trim() {
        return get(ItemStackComponentType.TRIM);
    }

    public DyedColorComponent dyedColor() {
        return get(ItemStackComponentType.DYED_COLOR);
    }

    public ProfileComponent profile() {
        return get(ItemStackComponentType.PROFILE);
    }

    public RecipesComponent recipes() {
        return get(ItemStackComponentType.RECIPES);
    }
}
