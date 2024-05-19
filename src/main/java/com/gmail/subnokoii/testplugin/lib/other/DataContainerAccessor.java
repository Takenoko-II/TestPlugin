package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
            if (value instanceof String) {
                container.set(key, PersistentDataType.STRING, (String) value);
            }
            else if (value instanceof Integer) {
                container.set(key, PersistentDataType.INTEGER, (Integer) value);
            }
            else if (value instanceof Float) {
                container.set(key, PersistentDataType.FLOAT, (Float) value);
            }
            else if (value instanceof Double) {
                container.set(key, PersistentDataType.DOUBLE, (Double) value);
            }
            else if (value instanceof Long) {
                container.set(key, PersistentDataType.LONG, (Long) value);
            }
            else if (value instanceof Byte) {
                container.set(key, PersistentDataType.BYTE, (Byte) value);
            }
            else if (value instanceof Boolean) {
                container.set(key, PersistentDataType.BOOLEAN, (Boolean) value);
            }
            else if (value instanceof Short) {
                container.set(key, PersistentDataType.SHORT, (Short) value);
            }
            else if (value instanceof PersistentDataContainer) {
                container.set(key, PersistentDataType.TAG_CONTAINER, (PersistentDataContainer) value);
            }
            else if (value instanceof DataContainerAccessor) {
                container.set(key, PersistentDataType.TAG_CONTAINER, ((DataContainerAccessor) value).container);
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
        final PersistentDataType<?, ?>[] types = new PersistentDataType[]{
            PersistentDataType.TAG_CONTAINER,
            PersistentDataType.STRING,
            PersistentDataType.BOOLEAN,
            PersistentDataType.INTEGER,
            PersistentDataType.FLOAT,
            PersistentDataType.DOUBLE,
            PersistentDataType.LONG,
            PersistentDataType.BYTE,
            PersistentDataType.SHORT
        };

        for (final PersistentDataType<?, ?> type : types) {
            if (Objects.equals(get(path, type), value)) {
                return true;
            }
        }

        return false;
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
     * 指定パスにString型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return String型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable String getString(String path) {
        return get(path, PersistentDataType.STRING);
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
     * 指定パスにint型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return int型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Integer getInteger(String path) {
        return get(path, PersistentDataType.INTEGER);
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
     * 指定パスにbyte型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return byte型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Byte getByte(String path) {
        return get(path, PersistentDataType.BYTE);
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
     * 指定パスにshort型の値があればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return short型の値、値が存在しないか型が違っていればnull
     */
    public @Nullable Short getShort(String path) {
        return get(path, PersistentDataType.SHORT);
    }
}
