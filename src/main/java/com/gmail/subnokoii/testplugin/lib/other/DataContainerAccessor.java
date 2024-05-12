package com.gmail.subnokoii.testplugin.lib.other;

import com.gmail.subnokoii.testplugin.TestPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DataContainerAccessor {
    private final PersistentDataContainer container;

    public DataContainerAccessor(PersistentDataContainer container) {
        this.container = container;
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

    public PersistentDataContainer set(String path, Object value) {
        final List<String> keys = new ArrayList<>(Arrays.asList(path.split("\\.")));

        loop(keys, this.container, value);

        return this.container;
    }

    private PersistentDataContainer createNewContainer() {
        return new ItemStack(Material.APPLE).getItemMeta().getPersistentDataContainer();
    }

    private PersistentDataContainer loop(List<String> keys, PersistentDataContainer container, Object value) {
        final NamespacedKey key = new NamespacedKey(TestPlugin.get(), keys.get(0));
        keys.remove(0);

        if (!keys.isEmpty()) {
            PersistentDataContainer subContainer = null;

            try { subContainer = container.get(key, PersistentDataType.TAG_CONTAINER); }
            catch (IllegalArgumentException ignored) {}

            if (subContainer == null) {
                subContainer = createNewContainer();
            }

            container.set(key, PersistentDataType.TAG_CONTAINER, loop(keys, subContainer, value));

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
                throw new RuntimeException("この型の値はDataContainerAccessorでは扱えません");
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
}
