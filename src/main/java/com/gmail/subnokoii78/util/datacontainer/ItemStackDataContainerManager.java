package com.gmail.subnokoii78.util.datacontainer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class ItemStackDataContainerManager extends DataContainerManager {
    private final ItemStack itemStack;

    /**
     * アイテム内のカスタムデータを操作するためのオブジェクトを作成します。
     * @param itemStack 使用するアイテム
     */
    public ItemStackDataContainerManager(ItemStack itemStack) {
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStackが必要ですがnullが渡されました");
        }

        this.itemStack = itemStack;
    }

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        return new DataContainerCompound(meta.getPersistentDataContainer()).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    @Override
    public ItemStackDataContainerManager set(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return this;

        new DataContainerCompound(meta.getPersistentDataContainer()).set(path, value);

        itemStack.setItemMeta(meta);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    @Override
    public ItemStackDataContainerManager delete(String path) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return this;

        new DataContainerCompound(meta.getPersistentDataContainer()).delete(path);

        itemStack.setItemMeta(meta);

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    @Override
    public boolean has(String path) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerCompound(meta.getPersistentDataContainer()).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    @Override
    public boolean equals(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerCompound(meta.getPersistentDataContainer()).equals(path, value);
    }

    /**
     * データをJSONに変換します。
     *
     * @return JSON化されたPersistentDataContainer
     */
    @Override
    public TextComponent toJson() {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return Component.text("{\n}").color(NamedTextColor.WHITE);

        return new DataContainerCompound(meta.getPersistentDataContainer()).toJson();
    }

    /**
     * すべてのキーを取得します。
     * @return キー文字列の配列
     */
    public String[] getAllKeys() {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return new String[0];

        return new DataContainerCompound(meta.getPersistentDataContainer()).getAllKeys();
    }
}
