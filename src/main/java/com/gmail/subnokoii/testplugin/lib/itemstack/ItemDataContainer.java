package com.gmail.subnokoii.testplugin.lib.itemstack;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDataContainer {
    private final ItemStack itemStack;

    public ItemDataContainer(ItemStack itemStack) {
        if (itemStack == null) {
            throw new RuntimeException();
        }

        this.itemStack = itemStack;
    }

    private @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        final String[] keys = path.split("\\.");
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();

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

    public ItemStack set(String path, Object value) {
        final List<String> keys = new ArrayList<>(Arrays.asList(path.split("\\.")));
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return itemStack;

        setContainer(keys, meta.getPersistentDataContainer(), value);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private PersistentDataContainer createNew() {
        return new ItemStack(Material.APPLE).getItemMeta().getPersistentDataContainer();
    }

    private PersistentDataContainer setContainer(List<String> keys, PersistentDataContainer container, Object value) {
        TestPlugin.log("Server", keys.toString());

        final NamespacedKey key = new NamespacedKey(TestPlugin.get(), keys.get(0));
        keys.remove(0);

        if (!keys.isEmpty()) {
            PersistentDataContainer subContainer = null;

            try { subContainer = container.get(key, PersistentDataType.TAG_CONTAINER); }
            catch (IllegalArgumentException ignored) {}

            if (subContainer == null) {
                subContainer = createNew();
            }

            container.set(key, PersistentDataType.TAG_CONTAINER, setContainer(keys, subContainer, value));

            return container;
        }
        else {
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
            else {
                throw new RuntimeException();
            }

            return container;
        }
    }

    public boolean has(String path) {
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
            if (get(path, type) != null) {
                return true;
            }
        }

        return false;
    }

    public @Nullable String getString(String path) {
        return get(path, PersistentDataType.STRING);
    }

    public @Nullable Boolean getBoolean(String path) {
        return get(path, PersistentDataType.BOOLEAN);
    }

    public @Nullable Integer getInteger(String path) {
        return get(path, PersistentDataType.INTEGER);
    }

    public @Nullable Float getFloat(String path) {
        return get(path, PersistentDataType.FLOAT);
    }

    public @Nullable Double getDouble(String path) {
        return get(path, PersistentDataType.DOUBLE);
    }

    public @Nullable Byte getByte(String path) {
        return get(path, PersistentDataType.BYTE);
    }

    public @Nullable Long getLong(String path) {
        return get(path, PersistentDataType.LONG);
    }

    public @Nullable Short getShort(String path) {
        return get(path, PersistentDataType.SHORT);
    }
}
