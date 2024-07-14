package com.gmail.subnokoii78.testplugin.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public interface ItemStackComponent {
    /**
     * このコンポーネントが有効になっているかどうかを調べます。
     * @return 有効であれば真
     */
    boolean getEnabled();

    /**
     * このコンポーネントを無効化します。
     */
    void disable();

    /**
     * このコンポーネントがツールチップに表示されるかどうかを返します。
     * @return 表示されるなら真
     */
    boolean getShowInTooltip();

    /**
     * このコンポーネントをツールチップに表示するかどうかを変更します。
     * 一部のコンポーネントではサポートされていません。
     * @param flag 真であれば表示
     */
    void setShowInTooltip(boolean flag);

    /**
     * コンポーネントタイプを表現するクラス
     * @param <T> コンポーネントクラス
     */
    final class ComponentType<T extends ItemStackComponent> {
        private final Class<T> clazz;

        private static final Map<Class<? extends ItemStackComponent>, ComponentType<? extends ItemStackComponent>> types = new HashMap<>();

        private ComponentType(Class<T> type) {
            this.clazz = type;

            if (!types.containsKey(type)) {
                types.put(type, this);
            }
        }

        T getComponent(ItemMeta itemMeta) {
            try {
                final Constructor<T> constructor = clazz.getDeclaredConstructor(ItemMeta.class);
                constructor.setAccessible(true);
                return constructor.newInstance(itemMeta);
            }
            catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InvalidComponentTypeException(e.toString());
            }
        }

        /**
         * 全種類のコンポーネントタイプを取得します。
         * @return 読み取り専用のコンポーネントタイプSet
         */
        public static Set<ComponentType<? extends ItemStackComponent>> values() {
            return Set.copyOf(types.values());
        }

        /**
         * コンポーネントのIDを基にコンポーネントタイプを取得します。
         * @param id コンポーネントID
         * @return 対応するコンポーネントタイプ、存在しなければnull
         * @throws RuntimeException 基本この関数の使用者に原因はありません 発生した場合詳細はエラーメッセージを参照
         */
        public static @Nullable ItemStackComponent.ComponentType<? extends ItemStackComponent> get(String id) {
            for (final ComponentType<? extends ItemStackComponent> type : values()) {
                try {
                    final String componentId = (String) type.clazz.getField("COMPONENT_ID").get(null);

                    if (componentId.equals(id)) {
                        return type;
                    }
                }
                catch (NoSuchFieldException e) {
                    throw new RuntimeException("クラス '" + type.clazz.getSimpleName() + "' にフィールド COMPONENT_ID が存在しません: ", e);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException("クラス '" + type.clazz.getSimpleName() + "' のフィールド COMPONENT_ID にアクセスできません。アクセス修飾子を確認してください: ", e);
                }
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

        public static final ComponentType<EnchantmentsComponent> ENCHANTMENTS = new ComponentType<>(EnchantmentsComponent.class);

        public static final ComponentType<StoredEnchantmentsComponent> STORED_ENCHANTMENTS = new ComponentType<>(StoredEnchantmentsComponent.class);

        public static final ComponentType<PotionContentsComponent> POTION_CONTENTS = new ComponentType<>(PotionContentsComponent.class);

        public static final ComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = new ComponentType<>(CustomModelDataComponent.class);

        public static final ComponentType<TrimComponent> TRIM = new ComponentType<>(TrimComponent.class);

        public static final ComponentType<DyedColorComponent> DYED_COLOR = new ComponentType<>(DyedColorComponent.class);

        public static final ComponentType<ProfileComponent> PROFILE = new ComponentType<>(ProfileComponent.class);

        public static final ComponentType<RecipesComponent> RECIPES = new ComponentType<>(RecipesComponent.class);
    }
}
