package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
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
    public <T extends ItemStackComponent> T get(ComponentType<T> type) {
        return type.getComponentInstance(itemMeta);
    }

    /**
     * 指定の種類のコンポーネントが存在するかを取得します。
     * @param type コンポーネントタイプ
     * @return 存在すれば真
     */
    public boolean has(ComponentType<?> type) {
        return type.getComponentInstance(itemMeta).getEnabled();
    }

    /**
     * 指定の種類のコンポーネントを削除します。
     * @param type コンポーネントタイプ
     */
    public void delete(ComponentType<?> type) {
        type.getComponentInstance(itemMeta).disable();
    }

    /**
     * 持っている全種類のコンポーネントを取得します。
     * @return コンポーネントの配列
     */
    public ItemStackComponent[] getAll() {
        final List<ItemStackComponent> components = new ArrayList<>();

        for (final ComponentType<?> type : ComponentType.getAll()) {
            final ItemStackComponent component = type.getComponentInstance(itemMeta);

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
        for (final ComponentType<?> type : ComponentType.getAll()) {
            if (has(type)) return true;
        }

        return false;
    }

    /**
     * 全コンポーネントを削除します。
     */
    public void deleteAll() {
        for (final ComponentType<?> type : ComponentType.getAll()) {
            type.getComponentInstance(itemMeta).disable();
        }
    }

    public UnbreakableComponent unbreakable() {
        return new UnbreakableComponent(itemMeta);
    }

    public MaxStackSizeComponent maxStackSize() {
        return new MaxStackSizeComponent(itemMeta);
    }

    public EnchantmentGlintOverrideComponent enchantmentGlintOverride() {
        return new EnchantmentGlintOverrideComponent(itemMeta);
    }

    public CustomNameComponent customName() {
        return new CustomNameComponent(itemMeta);
    }

    public LoreComponent lore() {
        return new LoreComponent(itemMeta);
    }

    public AttributeModifiersComponent attributeModifiers() {
        return new AttributeModifiersComponent(itemMeta);
    }

    public RepairCostComponent repairCost() {
        return new RepairCostComponent(itemMeta);
    }

    public ItemNameComponent itemName() {
        return new ItemNameComponent(itemMeta);
    }

    public MaxDamageComponent maxDamage() {
        return new MaxDamageComponent(itemMeta);
    }

    /**
     * コンポーネントタイプを表現するクラス
     * @param <T> コンポーネントクラス
     */
    public static final class ComponentType<T extends ItemStackComponent> {
        private final Class<T> clazz;

        private static final List<ComponentType<?>> types = new ArrayList<>();

        private ComponentType(Class<T> type) {
            this.clazz = type;
            ComponentType.types.add(this);
        }

        private T getComponentInstance(ItemMeta itemMeta) {
            try {
                return clazz.getConstructor(ItemMeta.class).newInstance(itemMeta);
            }
            catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InvalidComponentTypeException(e.toString());
            }
        }

        /**
         * 全種類のコンポーネントタイプを取得します。
         * @return コンポーネントタイプの配列
         */
        public static ComponentType<?>[] getAll() {
            return types.toArray(ComponentType[]::new);
        }

        /**
         * コンポーネントのIDを基にコンポーネントタイプを取得します。
         * @param id コンポーネントID
         * @return 対応するコンポーネントタイプ、存在しなければnull
         */
        public static @Nullable ComponentType<?> get(String id) {
            for (final ComponentType<?> type : types) {
                try {
                    final String componentId = (String) type.clazz.getField("COMPONENT_ID").get(null);

                    if (componentId.equals(id)) {
                        return type;
                    }
                }
                catch (NoSuchFieldException | IllegalAccessException ignored) {}
            }

            return null;
        }

        public static final ComponentType<UnbreakableComponent> UNBREAKABLE = new ComponentType<>(UnbreakableComponent.class);

        public static final ComponentType<CustomNameComponent> CUSTOM_NAME = new ComponentType<>(CustomNameComponent.class);

        public static final ComponentType<ItemNameComponent> ITEM_NAME = new ComponentType<>(ItemNameComponent.class);

        public static final ComponentType<LoreComponent> LORE = new ComponentType<>(LoreComponent.class);

        public static final ComponentType<MaxStackSizeComponent> MAX_STACK_SIZE = new ComponentType<>(MaxStackSizeComponent.class);

        public static final ComponentType<RepairCostComponent> REPAIR_COST = new ComponentType<>(RepairCostComponent.class);

        public static final ComponentType<EnchantmentGlintOverrideComponent> ENCHANT_GLINT_OVERRIDE = new ComponentType<>(EnchantmentGlintOverrideComponent.class);

        public static final ComponentType<AttributeModifiersComponent> ATTRIBUTE_MODIFIERS = new ComponentType<>(AttributeModifiersComponent.class);

        public static final ComponentType<MaxDamageComponent> MAX_DAMAGE = new ComponentType<>(MaxDamageComponent.class);
    }
}
