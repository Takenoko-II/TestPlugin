package com.gmail.subnokoii78.util.datacontainer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class DataContainerManager {
    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型の値あるいはnull
     */
    abstract @Nullable <P, C> C get(String path, PersistentDataType<P, C> type);

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
    public final @Nullable DataContainerCompound getCompound(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        if (dataContainer == null) return null;

        return new DataContainerCompound(dataContainer);
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
    public final @Nullable DataContainerCompound[] getCompoundArray(String path) {
        final List<PersistentDataContainer> list = get(path, PersistentDataType.LIST.dataContainers());

        if (list == null) return null;

        return list.stream().map(DataContainerCompound::new).toArray(DataContainerCompound[]::new);
    }

    /**
     * 指定パスに何かデータがあればそれを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return Object型の値、値が存在しなければnull
     */
    public final @Nullable Object getObject(String path) {
        for (final PersistentDataType<?, ?> dataType : PERSISTENT_DATA_TYPES) {
            final Object value = get(path, dataType);

            if (value == null) continue;

            return value;
        }

        return null;
    }

    /**
     * データをJSONに変換します。
     *
     * @return JSON化されたPersistentDataContainer
     */
    public abstract TextComponent toJson();

    /**
     * すべてのキーを取得します。
     * @return キー文字列の配列
     */
    public abstract  String[] getAllKeys();

    /**
     * 特定の名前空間のキーを取得します。
     * @return キー文字列の配列
     */
    public String[] getKeys(String namespace) {
        return Arrays.stream(getAllKeys()).filter(t -> t.startsWith(namespace + ":")).toArray(String[]::new);
    }

    private static TextComponent stringify(Object value, int indentation) {
        if (value instanceof PersistentDataContainer) {
            final String[] keys = new DataContainerCompound((PersistentDataContainer) value).getAllKeys();

            TextComponent component = Component.text("{").color(NamedTextColor.WHITE);

            for (int i = 0; i < keys.length; i++) {
                final String key = keys[i];

                boolean foundTypeFlag = false;

                for (final PersistentDataType<?, ?> type : PERSISTENT_DATA_TYPES) {
                    final Object childValue = new DataContainerCompound((PersistentDataContainer) value).get(key, type);

                    if (childValue != null) {
                        component = component.append(Component.text("\n"))
                        .append(Component.text("  ".repeat(indentation)))
                        .append(Component.text("\"").color(NamedTextColor.WHITE))
                        .append(Component.text(key).color(TextColor.color(NamedTextColor.AQUA))
                        .append(Component.text("\": ").color(NamedTextColor.WHITE)))
                        .append(stringify(childValue, indentation + 1));

                        if (i != keys.length - 1) {
                            component = component.append(Component.text(",").color(NamedTextColor.WHITE));
                        }

                        foundTypeFlag = true;

                        break;
                    }
                }

                if (!foundTypeFlag) {
                    component = component.append(Component.text("\n"))
                    .append(Component.text("  ".repeat(indentation)))
                    .append(Component.text("\"").color(NamedTextColor.WHITE))
                    .append(Component.text(key).color(TextColor.color(NamedTextColor.AQUA))
                    .append(Component.text("\": ").color(NamedTextColor.WHITE)))
                    .append(
                        Component.text("<").color(NamedTextColor.GRAY)
                        .append(Component.text("Unsupported Type Value").color(NamedTextColor.RED))
                        .append(Component.text(">").color(NamedTextColor.GRAY))
                    );
                }
            }

            return component
            .append(Component.text("\n"))
            .append(Component.text("  ".repeat(indentation - 1)))
            .append(Component.text("}").color(NamedTextColor.WHITE));
        }
        else if (value instanceof String) {
            return Component.text("\"").color(NamedTextColor.WHITE)
            .append(Component.text(value.toString()).color(NamedTextColor.GREEN))
            .append(Component.text("\"").color(NamedTextColor.WHITE));
        }
        else if (value instanceof Integer) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD);
        }
        else if (value instanceof Float) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD)
            .append(Component.text("f").color(NamedTextColor.RED));
        }
        else if (value instanceof Byte) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD)
            .append(Component.text("b").color(NamedTextColor.RED));
        }
        else if (value instanceof Boolean) {
            if ((Boolean) value) {
                return Component.text("1").color(NamedTextColor.GOLD)
                .append(Component.text("b").color(NamedTextColor.RED));
            }
            else {
                return Component.text("0").color(NamedTextColor.GOLD)
                .append(Component.text("b").color(NamedTextColor.RED));
            }
        }
        else if (value instanceof Double) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD)
            .append(Component.text("d").color(NamedTextColor.RED));
        }
        else if (value instanceof Long) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD)
            .append(Component.text("L").color(NamedTextColor.RED));
        }
        else if (value instanceof Short) {
            return Component.text(value.toString()).color(NamedTextColor.GOLD)
            .append(Component.text("s").color(NamedTextColor.RED));
        }
        else if (value instanceof List) {
            final Object[] array = ((List<?>) value).toArray(Object[]::new);

            TextComponent component = Component.text("[\n").color(NamedTextColor.WHITE);

            for (int i = 0; i < array.length; i++) {
                final Object element = array[i];

                component = component
                .append(Component.text("  ".repeat(indentation)))
                .append(stringify(element, indentation + 1));

                if (i != array.length - 1) {
                    component = component.append(Component.text(",\n").color(NamedTextColor.WHITE));
                }
            }

            return component
            .append(Component.text("\n"))
            .append(Component.text("  ".repeat(indentation - 1) + "]").color(NamedTextColor.WHITE));
        }
        else return Component.text("<").color(NamedTextColor.GRAY)
            .append(Component.text(value.getClass().getSimpleName()).color(TextColor.color(0, 255, 202)))
            .append(Component.text(">").color(NamedTextColor.GRAY));
    }

    /**
     * データコンテナに保存可能な値を文字列化します。
     * @param value 任意の値
     * @return 文字列化された値
     */
    public static TextComponent stringify(Object value) {
        return stringify(value, 1);
    }

    /**
     * PersistentDataContainerを新たに作成します。
     * @return 新しい空のPersistentDataContainer
     */
    static PersistentDataContainer container() {
        return Bukkit.getWorlds().get(0).getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
    }

    /**
     * 空のコンパウンドを新たに作成します。
     * @return 空のコンパウンド
     */
    public static DataContainerCompound compound() {
        return new DataContainerCompound(container());
    }

    /**
     * ほぼ全ての基本データ型のリスト
     */
    static final PersistentDataType<?, ?>[] PERSISTENT_DATA_TYPES = new PersistentDataType[]{
        PersistentDataType.BYTE,
        PersistentDataType.BOOLEAN,
        PersistentDataType.SHORT,
        PersistentDataType.INTEGER,
        PersistentDataType.LONG,
        PersistentDataType.FLOAT,
        PersistentDataType.DOUBLE,
        PersistentDataType.STRING,
        PersistentDataType.TAG_CONTAINER,
        PersistentDataType.LIST.bytes(),
        PersistentDataType.LIST.booleans(),
        PersistentDataType.LIST.shorts(),
        PersistentDataType.LIST.integers(),
        PersistentDataType.LIST.longs(),
        PersistentDataType.LIST.floats(),
        PersistentDataType.LIST.doubles(),
        PersistentDataType.LIST.strings(),
        PersistentDataType.LIST.dataContainers()
    };
}
