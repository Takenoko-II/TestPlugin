package com.gmail.subnokoii.testplugin.lib.datacontainer;

import com.gmail.subnokoii.testplugin.lib.file.BinaryFileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class FileDataContainerManager extends DataContainerManager {
    private final String filePath;

    /**
     * バイナリファイル内のカスタムデータを操作するためのオブジェクトを作成します。
     * @param path 使用するファイルのパス
     */
    public FileDataContainerManager(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Stringが必要ですがnullが渡されました");
        }

        this.filePath = path;
    }

    private @Nullable PersistentDataContainer getPersistentDataContainer() {
        BinaryFileUtils.create(filePath);

        final byte[] data = BinaryFileUtils.read(filePath);

        if (data == null) return null;

        final PersistentDataContainer empty = DataContainerManager.newContainer();

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

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    public <P, C> @Nullable C get(String path, PersistentDataType<P, C> type) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return null;

        return new DataContainerAccessor(container).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    @Override
    public FileDataContainerManager set(String path, Object value) {
        PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) {
            container = DataContainerManager.newContainer();
        }

        new DataContainerAccessor(container).set(path, value);

        setPersistentDataContainer(container);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    @Override
    public FileDataContainerManager delete(String path) {
        PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) {
            container = DataContainerManager.newContainer();
        }

        new DataContainerAccessor(container).delete(path);

        setPersistentDataContainer(container);
        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    @Override
    public boolean has(String path) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return false;

        return new DataContainerAccessor(container).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    @Override
    public boolean equals(String path, Object value) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return false;

        return new DataContainerAccessor(container).equals(path, value);
    }

    /**
     * データをJSONに変換します。
     * @return JSON化されたPersistentDataContainer
     */
    @Override
    public Component toJson() {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return Component.text("{\n}").color(NamedTextColor.WHITE);

        return new DataContainerAccessor(container).toJson();
    }
}
