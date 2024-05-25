package com.gmail.subnokoii.testplugin.lib.datacontainer;

import com.gmail.subnokoii.testplugin.lib.file.BinaryFileUtils;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class FileDataContainerManager extends DataContainerManager {
    private final String filePath;

    public FileDataContainerManager(JavaPlugin plugin) {
        this.filePath = "plugins/" + plugin.getName() + ".bin";
    }

    public FileDataContainerManager(String path) {
        this.filePath = path;
    }

    private @Nullable PersistentDataContainer getPersistentDataContainer() {
        BinaryFileUtils.create(filePath);

        final byte[] data = BinaryFileUtils.read(filePath);

        if (data == null) return null;

        final PersistentDataContainer empty = Bukkit.getWorlds().get(0).getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();

        try {
            empty.readFromBytes(data);
        }
        catch (IOException e) {
            return null;
        }

        return empty;
    }

    private void setPersistentDataContainer(PersistentDataContainer container) {
        BinaryFileUtils.create(filePath);

        try {
            final byte[] data = container.serializeToBytes();

            BinaryFileUtils.overwrite(filePath, data);
        }
        catch (IOException e) {
            throw new RuntimeException("PersistentDataContainerのシリアライズに失敗しました");
        }
    }

    @Override
    public <P, C> @Nullable C get(String path, PersistentDataType<P, C> type) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return null;

        return new DataContainerAccessor(container).get(path, type);
    }

    @Override
    public FileDataContainerManager set(String path, Object value) {
        PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) {
            container = Bukkit.getWorlds().get(0).getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
        }

        new DataContainerAccessor(container).set(path, value);

        setPersistentDataContainer(container);

        return this;
    }

    @Override
    public FileDataContainerManager delete(String path) {
        PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) {
            container = Bukkit.getWorlds().get(0).getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
        }

        new DataContainerAccessor(container).delete(path);

        setPersistentDataContainer(container);

        return this;
    }

    @Override
    public boolean has(String path) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return false;

        return new DataContainerAccessor(container).has(path);
    }

    @Override
    public boolean equals(String path, Object value) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return false;

        return new DataContainerAccessor(container).equals(path, value);
    }
}
