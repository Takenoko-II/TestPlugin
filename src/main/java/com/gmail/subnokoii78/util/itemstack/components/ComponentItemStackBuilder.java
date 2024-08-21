package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

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
    public <T extends ItemStackComponent> T getComponent(ItemStackComponentType<T> type) {
        return type.getInstance(itemMeta);
    }

    /**
     * 持っている全種類のコンポーネントを取得します。
     * @return コンポーネントの {@link Set}
     */
    public Set<ItemStackComponent> getAllComponents() {
        final Set<ItemStackComponent> components = new HashSet<>();

        for (final ItemStackComponentType<?> type : ItemStackComponentType.values()) {
            final ItemStackComponent component = type.getInstance(itemMeta);
            if (component.isEnabled()) components.add(component);
        }

        return components;
    }

    /**
     * 全コンポーネントを無効化します。
     */
    public void disableAllComponents() {
        for (final ItemStackComponentType<?> type : ItemStackComponentType.values()) {
            type.getInstance(itemMeta).disable();
        }
    }

    public UnbreakableComponent unbreakable() {
        return getComponent(ItemStackComponentType.UNBREAKABLE);
    }

    public MaxStackSizeComponent maxStackSize() {
        return getComponent(ItemStackComponentType.MAX_STACK_SIZE);
    }

    public EnchantmentGlintOverrideComponent enchantmentGlintOverride() {
        return getComponent(ItemStackComponentType.ENCHANT_GLINT_OVERRIDE);
    }

    public CustomNameComponent customName() {
        return getComponent(ItemStackComponentType.CUSTOM_NAME);
    }

    public LoreComponent lore() {
        return getComponent(ItemStackComponentType.LORE);
    }

    public AttributeModifiersComponent attributeModifiers() {
        return getComponent(ItemStackComponentType.ATTRIBUTE_MODIFIERS);
    }

    public RepairCostComponent repairCost() {
        return getComponent(ItemStackComponentType.REPAIR_COST);
    }

    public ItemNameComponent itemName() {
        return getComponent(ItemStackComponentType.ITEM_NAME);
    }

    public MaxDamageComponent maxDamage() {
        return getComponent(ItemStackComponentType.MAX_DAMAGE);
    }

    public EnchantmentsComponent enchantments() {
        return getComponent(ItemStackComponentType.ENCHANTMENTS);
    }

    public StoredEnchantmentsComponent storedEnchantments() {
        return getComponent(ItemStackComponentType.STORED_ENCHANTMENTS);
    }

    public PotionContentsComponent potionContents() {
        return getComponent(ItemStackComponentType.POTION_CONTENTS);
    }

    public CustomModelDataComponent customModelData() {
        return getComponent(ItemStackComponentType.CUSTOM_MODEL_DATA);
    }

    public TrimComponent trim() {
        return getComponent(ItemStackComponentType.TRIM);
    }

    public DyedColorComponent dyedColor() {
        return getComponent(ItemStackComponentType.DYED_COLOR);
    }

    public ProfileComponent profile() {
        return getComponent(ItemStackComponentType.PROFILE);
    }

    public RecipesComponent recipes() {
        return getComponent(ItemStackComponentType.RECIPES);
    }

    public FoodComponent food() {
        return getComponent(ItemStackComponentType.FOOD);
    }
}
