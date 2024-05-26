package com.gmail.subnokoii.testplugin.lib.datacontainer;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class DataContainerManager {
    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型の値あるいはnull
     */
    public abstract @Nullable <P, C> C get(String path, PersistentDataType<P, C> type);

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    public abstract DataContainerManager set(String path, Object value);

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    public abstract DataContainerManager delete(String path);

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    public abstract boolean has(String path);

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    public abstract boolean equals(String path, Object value);

    /**
     * 指定パスにboolean型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return boolean型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Boolean getBoolean(String path) {
        return get(path, PersistentDataType.BOOLEAN);
    }

    /**
     * 指定パスにbyte型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return byte型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Byte getByte(String path) {
        return get(path, PersistentDataType.BYTE);
    }

    /**
     * 指定パスにshort型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return short型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Short getShort(String path) {
        return get(path, PersistentDataType.SHORT);
    }

    /**
     * 指定パスにint型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return int型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Integer getInteger(String path) {
        return get(path, PersistentDataType.INTEGER);
    }

    /**
     * 指定パスにlong型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return long型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Long getLong(String path) {
        return get(path, PersistentDataType.LONG);
    }

    /**
     * 指定パスにfloat型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return float型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Float getFloat(String path) {
        return get(path, PersistentDataType.FLOAT);
    }

    /**
     * 指定パスにdouble型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return double型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable Double getDouble(String path) {
        return get(path, PersistentDataType.DOUBLE);
    }

    /**
     * 指定パスにString型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return String型の値、値が存在しないか型が違っていればnull
     */
    public final @Nullable String getString(String path) {
        return get(path, PersistentDataType.STRING);
    }

    /**
     * 指定パスにコンパウンドタグがあればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグ、値が存在しないか型が違っていればnull
     */
    public final @Nullable DataContainerAccessor getCompound(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        if (dataContainer == null) return null;

        return new DataContainerAccessor(dataContainer);
    }

    /**
     * 指定パスにコンパウンドタグがあればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグ、値が存在しないか型が違っていれば空のコンパウンドタグ
     */
    public final @NotNull DataContainerAccessor getCompoundOrEmpty(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        return new DataContainerAccessor(Objects.requireNonNullElse(dataContainer, new ItemStack(Material.APPLE).getItemMeta().getPersistentDataContainer()));
    }

    /**
     * 指定パスにboolean型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return boolean型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Boolean[] getBooleanArray(String path) {
        final List<Boolean> list = get(path, PersistentDataType.LIST.booleans());

        if (list == null) return null;

        return list.toArray(Boolean[]::new);
    }

    /**
     * 指定パスにbyte型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return byte型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Byte[] getByteArray(String path) {
        final List<Byte> list = get(path, PersistentDataType.LIST.bytes());

        if (list == null) return null;

        return list.toArray(Byte[]::new);
    }

    /**
     * 指定パスにshort型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return short型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Short[] getShortArray(String path) {
        final List<Short> list = get(path, PersistentDataType.LIST.shorts());

        if (list == null) return null;

        return list.toArray(Short[]::new);
    }

    /**
     * 指定パスにint型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return int型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Integer[] getIntegerArray(String path) {
        final List<Integer> list = get(path, PersistentDataType.LIST.integers());

        if (list == null) return null;

        return list.toArray(Integer[]::new);
    }

    /**
     * 指定パスにlong型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return long型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Long[] getLongArray(String path) {
        final List<Long> list = get(path, PersistentDataType.LIST.longs());

        if (list == null) return null;

        return list.toArray(Long[]::new);
    }

    /**
     * 指定パスにfloat型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return float型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Float[] getFloatArray(String path) {
        final List<Float> list = get(path, PersistentDataType.LIST.floats());

        if (list == null) return null;

        return list.toArray(Float[]::new);
    }

    /**
     * 指定パスにdouble型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return double型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable Double[] getDoubleArray(String path) {
        final List<Double> list = get(path, PersistentDataType.LIST.doubles());

        if (list == null) return null;

        return list.toArray(Double[]::new);
    }

    /**
     * 指定パスにString型の配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return String型の配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable String[] getStringArray(String path) {
        final List<String> list = get(path, PersistentDataType.LIST.strings());

        if (list == null) return null;

        return list.toArray(String[]::new);
    }

    /**
     * 指定パスにコンパウンドタグの配列があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return コンパウンドタグの配列、値が存在しないか型が違っていればnull
     */
    public final @Nullable DataContainerAccessor[] getCompoundArray(String path) {
        final List<PersistentDataContainer> list = get(path, PersistentDataType.LIST.dataContainers());

        if (list == null) return null;

        return list.stream().map(DataContainerAccessor::new).toArray(DataContainerAccessor[]::new);
    }

    /**
     * データをJSONに変換します。
     * @return JSON化されたPersistentDataContainer
     */
    public abstract Component toJson();
}
