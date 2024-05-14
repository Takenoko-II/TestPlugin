package com.gmail.subnokoii.testplugin.lib.itemstack;

import com.gmail.subnokoii.testplugin.lib.other.DataContainerAccessor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class ItemStackDataContainerAccessor {
    private final ItemStack itemStack;

    public ItemStackDataContainerAccessor(ItemStack itemStack) {
        if (itemStack == null) {
            throw new IllegalArgumentException("引数がnullのためDataContainerへのアクセサを作成できません");
        }

        this.itemStack = itemStack;
    }

    private @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).get(path, type);
    }

    public ItemStack set(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return itemStack;

        new DataContainerAccessor(meta.getPersistentDataContainer()).set(path, value);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public boolean has(String path) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).has(path);
    }

    public boolean equals(String path, Object value) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return false;

        return new DataContainerAccessor(meta.getPersistentDataContainer()).equals(path, value);
    }

    public @Nullable DataContainerAccessor getCompound(String path) {
        final PersistentDataContainer dataContainer = get(path, PersistentDataType.TAG_CONTAINER);

        if (dataContainer == null) return null;

        return new DataContainerAccessor(dataContainer);
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
