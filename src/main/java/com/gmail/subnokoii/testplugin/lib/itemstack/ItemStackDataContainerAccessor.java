package com.gmail.subnokoii.testplugin.lib.itemstack;

import com.gmail.subnokoii.testplugin.lib.other.DataContainerAccessor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class ItemStackDataContainerAccessor {
    private final ItemStack itemStack;

    public ItemStackDataContainerAccessor(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    public ItemStackDataContainerAccessor set(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return this;

        new DataContainerAccessor(meta.getPersistentDataContainer()).set(path, value);

        itemStack.setItemMeta(meta);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    public ItemStackDataContainerAccessor delete(String path) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return this;

        new DataContainerAccessor(meta.getPersistentDataContainer()).delete(path);

        itemStack.setItemMeta(meta);

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    public boolean has(String path) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    public boolean equals(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).equals(path, value);
    }

    /**
     * 指定パスにboolean型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return boolean型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Boolean getBoolean(String path) {
        return get(path, PersistentDataType.BOOLEAN);
    }

    /**
     * 指定パスにbyte型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return byte型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Byte getByte(String path) {
        return get(path, PersistentDataType.BYTE);
    }

    /**
     * 指定パスにshort型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return short型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Short getShort(String path) {
        return get(path, PersistentDataType.SHORT);
    }

    /**
     * 指定パスにint型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return int型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Integer getInteger(String path) {
        return get(path, PersistentDataType.INTEGER);
    }

    /**
     * 指定パスにlong型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return long型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Long getLong(String path) {
        return get(path, PersistentDataType.LONG);
    }

    /**
     * 指定パスにfloat型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return float型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Float getFloat(String path) {
        return get(path, PersistentDataType.FLOAT);
    }

    /**
     * 指定パスにdouble型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return double型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Double getDouble(String path) {
        return get(path, PersistentDataType.DOUBLE);
    }

    /**
     * 指定パスにString型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return String型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable String getString(String path) {
        return get(path, PersistentDataType.STRING);
    }

    /**
     * 指定パスにコンパウンドタグがあればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグ、値が存在しないか型が違っていればnull
     */
    public @Nullable DataContainerAccessor getCompound(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        if (dataContainer == null) return null;

        return new DataContainerAccessor(dataContainer);
    }

    /**
     * 指定パスにコンパウンドタグがあればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグ、値が存在しないか型が違っていれば空のコンパウンドタグ
     */
    public @NotNull DataContainerAccessor getCompoundOrEmpty(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        return new DataContainerAccessor(Objects.requireNonNullElse(dataContainer, new ItemStack(Material.APPLE).getItemMeta().getPersistentDataContainer()));
    }

    /**
     * 指定パスにboolean型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return boolean型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Boolean[] getBooleanArray(String path) {
        final List<Boolean> list = get(path, PersistentDataType.LIST.booleans());

        if (list == null) return null;

        return list.toArray(Boolean[]::new);
    }

    /**
     * 指定パスにbyte型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return byte型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Byte[] getByteArray(String path) {
        final List<Byte> list = get(path, PersistentDataType.LIST.bytes());

        if (list == null) return null;

        return list.toArray(Byte[]::new);
    }

    /**
     * 指定パスにshort型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return short型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Short[] getShortArray(String path) {
        final List<Short> list = get(path, PersistentDataType.LIST.shorts());

        if (list == null) return null;

        return list.toArray(Short[]::new);
    }

    /**
     * 指定パスにint型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return int型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Integer[] getIntegerArray(String path) {
        final List<Integer> list = get(path, PersistentDataType.LIST.integers());

        if (list == null) return null;

        return list.toArray(Integer[]::new);
    }

    /**
     * 指定パスにlong型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return long型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Long[] getLongArray(String path) {
        final List<Long> list = get(path, PersistentDataType.LIST.longs());

        if (list == null) return null;

        return list.toArray(Long[]::new);
    }

    /**
     * 指定パスにfloat型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return float型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Float[] getFloatArray(String path) {
        final List<Float> list = get(path, PersistentDataType.LIST.floats());

        if (list == null) return null;

        return list.toArray(Float[]::new);
    }

    /**
     * 指定パスにdouble型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return double型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable Double[] getDoubleArray(String path) {
        final List<Double> list = get(path, PersistentDataType.LIST.doubles());

        if (list == null) return null;

        return list.toArray(Double[]::new);
    }

    /**
     * 指定パスにString型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return String型の配列、値が存在しないか型が違っていればnull
     */
    public @Nullable String[] getStringArray(String path) {
        final List<String> list = get(path, PersistentDataType.LIST.strings());

        if (list == null) return null;

        return list.toArray(String[]::new);
    }

    /**
     * 指定パスにコンパウンドタグの配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグの配列、値が存在しないか型が違っていればnull
     */
    public @Nullable DataContainerAccessor[] getCompoundArray(String path) {
        final List<PersistentDataContainer> list = get(path, PersistentDataType.LIST.dataContainers());

        if (list == null) return null;

        return list.stream().map(DataContainerAccessor::new).toArray(DataContainerAccessor[]::new);
    }
}