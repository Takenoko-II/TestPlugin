package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class DataContainerAccessor {
    private final PersistentDataContainer container;

    public DataContainerAccessor(PersistentDataContainer container) {
        this.container = container;
    }

    private PersistentDataContainer empty() {
        return container.getAdapterContext().newPersistentDataContainer();
    }

    private List<String> parsePath(String path) {
        return new ArrayList<>(Arrays.asList(path.split("\\.")));
    }

    private PersistentDataContainer access(List<String> keys, PersistentDataContainer container, BiConsumer<PersistentDataContainer, NamespacedKey> consumer) {
        final NamespacedKey key = new NamespacedKey(TestPlugin.get(), keys.get(0));
        keys.remove(0);

        if (!keys.isEmpty()) {
            PersistentDataContainer subContainer;

            if (container.has(key, PersistentDataType.TAG_CONTAINER)) {
                subContainer = container.get(key, PersistentDataType.TAG_CONTAINER);

                if (subContainer == null) {
                    // Never happens
                    subContainer = empty();
                }
            }
            else {
                subContainer = empty();
            }

            container.set(key, PersistentDataType.TAG_CONTAINER, access(keys, subContainer, consumer));
        }
        else {
            consumer.accept(container, key);
        }

        return container;
    }

    public @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        final String[] keys = path.split("\\.");

        PersistentDataContainer container = this.container;

        for (int i = 0; i < keys.length; i++) {
            final NamespacedKey namespacedKey = new NamespacedKey(TestPlugin.get(), keys[i]);

            if (container == null) {
                return null;
            }

            if (i == keys.length - 1) {
                try {
                    return container.get(namespacedKey, type);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            else {
                try {
                    container = container.get(namespacedKey, PersistentDataType.TAG_CONTAINER);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return このインスタンス
     */
    public DataContainerAccessor set(String path, Object value) {
        access(parsePath(path), this.container, (container, key) -> {
            if (value instanceof Boolean) {
                container.set(key, PersistentDataType.BOOLEAN, (Boolean) value);
            }
            else if (value instanceof Byte) {
                container.set(key, PersistentDataType.BYTE, (Byte) value);
            }
            else if (value instanceof Short) {
                container.set(key, PersistentDataType.SHORT, (Short) value);
            }
            else if (value instanceof Integer) {
                container.set(key, PersistentDataType.INTEGER, (Integer) value);
            }
            else if (value instanceof Long) {
                container.set(key, PersistentDataType.LONG, (Long) value);
            }
            else if (value instanceof Float) {
                container.set(key, PersistentDataType.FLOAT, (Float) value);
            }
            else if (value instanceof Double) {
                container.set(key, PersistentDataType.DOUBLE, (Double) value);
            }
            else if (value instanceof String) {
                container.set(key, PersistentDataType.STRING, (String) value);
            }
            else if (value instanceof PersistentDataContainer) {
                container.set(key, PersistentDataType.TAG_CONTAINER, (PersistentDataContainer) value);
            }
            else if (value instanceof DataContainerAccessor) {
                container.set(key, PersistentDataType.TAG_CONTAINER, ((DataContainerAccessor) value).container);
            }
            else if (value instanceof Boolean[]) {
                container.set(key, PersistentDataType.LIST.booleans(), List.of((Boolean[]) value));
            }
            else if (value instanceof Byte[]) {
                container.set(key, PersistentDataType.LIST.bytes(), List.of((Byte[]) value));
            }
            else if (value instanceof Integer[]) {
                container.set(key, PersistentDataType.LIST.integers(), List.of((Integer[]) value));
            }
            else if (value instanceof Long[]) {
                container.set(key, PersistentDataType.LIST.longs(), List.of((Long[]) value));
            }
            else if (value instanceof Float[]) {
                container.set(key, PersistentDataType.LIST.floats(), List.of((Float[]) value));
            }
            else if (value instanceof Double[]) {
                container.set(key, PersistentDataType.LIST.doubles(), List.of((Double[]) value));
            }
            else if (value instanceof String[]) {
                container.set(key, PersistentDataType.LIST.strings(), List.of((String[]) value));
            }
            else if (value instanceof PersistentDataContainer[]) {
                container.set(key, PersistentDataType.LIST.dataContainers(), List.of((PersistentDataContainer[]) value));
            }
            else if (value instanceof DataContainerAccessor[]) {
                final PersistentDataContainer[] list = Arrays
                .stream(((DataContainerAccessor[]) value))
                .map(accessor -> accessor.container)
                .toArray(PersistentDataContainer[]::new);

                container.set(key, PersistentDataType.LIST.dataContainers(), List.of(list));
            }
            else {
                throw new IllegalArgumentException("その型の値はDataContainerAccessorにはアクセスできません");
            }
        });

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return このインスタンス
     */
    public DataContainerAccessor delete(String path) {
        access(parsePath(path), container, (container, key) -> {
            container.remove(key);
        });

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    public boolean has(String path) {
        AtomicBoolean flag = new AtomicBoolean(false);

        access(parsePath(path), container, (container, key) -> {
            flag.set(container.has(key));
        });

        return flag.get();
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    public boolean equals(String path, Object value) {
        final PersistentDataType<?, ?>[] dataTypes = new PersistentDataType[]{
        PersistentDataType.BOOLEAN,
        PersistentDataType.BYTE,
        PersistentDataType.SHORT,
        PersistentDataType.INTEGER,
        PersistentDataType.LONG,
        PersistentDataType.FLOAT,
        PersistentDataType.DOUBLE,
        PersistentDataType.STRING,
        PersistentDataType.TAG_CONTAINER,
        PersistentDataType.LIST.booleans(),
        PersistentDataType.LIST.bytes(),
        PersistentDataType.LIST.shorts(),
        PersistentDataType.LIST.integers(),
        PersistentDataType.LIST.longs(),
        PersistentDataType.LIST.floats(),
        PersistentDataType.LIST.doubles(),
        PersistentDataType.LIST.strings(),
        PersistentDataType.LIST.dataContainers()
        };

        for (final PersistentDataType<?, ?> type : dataTypes) {
            if (Objects.equals(get(path, type), value)) {
                return true;
            }
        }

        return false;
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

        return new DataContainerAccessor(Objects.requireNonNullElse(dataContainer, empty()));
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

    public static DataContainerAccessor create() {
        return new DataContainerAccessor(new ItemStack(Material.APPLE).getItemMeta().getPersistentDataContainer());
    }
}
