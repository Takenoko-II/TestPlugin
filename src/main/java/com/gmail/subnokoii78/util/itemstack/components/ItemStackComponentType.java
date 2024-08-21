package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * コンポーネントタイプを表現するクラス
 * @param <T> コンポーネントクラス
 */
public final class ItemStackComponentType<T extends ItemStackComponent> {
    private final Class<T> clazz;

    private static final Map<Class<? extends ItemStackComponent>, ItemStackComponentType<? extends ItemStackComponent>> types = new HashMap<>();

    private ItemStackComponentType(Class<T> type) {
        this.clazz = type;

        if (!types.containsKey(type)) {
            types.put(type, this);
        }
    }

    T getInstance(ItemMeta itemMeta) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor(ItemMeta.class);
            constructor.setAccessible(true);
            return constructor.newInstance(itemMeta);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InvalidComponentTypeException("コンポーネントの取得に失敗しました", e);
        }
    }

    /**
     * 全種類のコンポーネントタイプを取得します。
     *
     * @return 読み取り専用のコンポーネントタイプSet
     */
    public static Set<ItemStackComponentType<? extends ItemStackComponent>> values() {
        return Set.copyOf(types.values());
    }

    public static final ItemStackComponentType<UnbreakableComponent> UNBREAKABLE = new ItemStackComponentType<>(UnbreakableComponent.class);

    public static final ItemStackComponentType<CustomNameComponent> CUSTOM_NAME = new ItemStackComponentType<>(CustomNameComponent.class);

    public static final ItemStackComponentType<ItemNameComponent> ITEM_NAME = new ItemStackComponentType<>(ItemNameComponent.class);

    public static final ItemStackComponentType<LoreComponent> LORE = new ItemStackComponentType<>(LoreComponent.class);

    public static final ItemStackComponentType<MaxStackSizeComponent> MAX_STACK_SIZE = new ItemStackComponentType<>(MaxStackSizeComponent.class);

    public static final ItemStackComponentType<RepairCostComponent> REPAIR_COST = new ItemStackComponentType<>(RepairCostComponent.class);

    public static final ItemStackComponentType<EnchantmentGlintOverrideComponent> ENCHANT_GLINT_OVERRIDE = new ItemStackComponentType<>(EnchantmentGlintOverrideComponent.class);

    public static final ItemStackComponentType<AttributeModifiersComponent> ATTRIBUTE_MODIFIERS = new ItemStackComponentType<>(AttributeModifiersComponent.class);

    public static final ItemStackComponentType<MaxDamageComponent> MAX_DAMAGE = new ItemStackComponentType<>(MaxDamageComponent.class);

    public static final ItemStackComponentType<EnchantmentsComponent> ENCHANTMENTS = new ItemStackComponentType<>(EnchantmentsComponent.class);

    public static final ItemStackComponentType<StoredEnchantmentsComponent> STORED_ENCHANTMENTS = new ItemStackComponentType<>(StoredEnchantmentsComponent.class);

    public static final ItemStackComponentType<PotionContentsComponent> POTION_CONTENTS = new ItemStackComponentType<>(PotionContentsComponent.class);

    public static final ItemStackComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = new ItemStackComponentType<>(CustomModelDataComponent.class);

    public static final ItemStackComponentType<TrimComponent> TRIM = new ItemStackComponentType<>(TrimComponent.class);

    public static final ItemStackComponentType<DyedColorComponent> DYED_COLOR = new ItemStackComponentType<>(DyedColorComponent.class);

    public static final ItemStackComponentType<ProfileComponent> PROFILE = new ItemStackComponentType<>(ProfileComponent.class);

    public static final ItemStackComponentType<RecipesComponent> RECIPES = new ItemStackComponentType<>(RecipesComponent.class);

    public static final ItemStackComponentType<FoodComponent> FOOD = new ItemStackComponentType<>(FoodComponent.class);
}
