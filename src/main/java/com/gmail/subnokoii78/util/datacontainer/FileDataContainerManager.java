package com.gmail.subnokoii78.util.datacontainer;

import com.gmail.subnokoii78.util.file.BinaryFileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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

        final byte[] data = BinaryFileUtils.read(filePath);

        if (data == null) {
            setPersistentDataContainer(container());
        }
        else if (data.length == 0) {
            setPersistentDataContainer(container());
        }
    }

    private @Nullable PersistentDataContainer getPersistentDataContainer() {
        BinaryFileUtils.create(filePath);

        final byte[] data = BinaryFileUtils.read(filePath);

        if (data == null) return null;

        final PersistentDataContainer empty = container();

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
            throw new RuntimeException("PersistentDataContainerのシリアライズまたはファイルへの書き込みに失敗しました", e);
        }
    }

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    <P, C> @Nullable C get(String path, PersistentDataType<P, C> type) {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return null;

        return new DataContainerCompound(container).get(path, type);
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
            container = container();
        }

        new DataContainerCompound(container).set(path, value);

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
            container = container();
        }

        new DataContainerCompound(container).delete(path);

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

        return new DataContainerCompound(container).has(path);
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

        return new DataContainerCompound(container).equals(path, value);
    }

    /**
     * データをJSONに変換します。
     *
     * @return JSON化されたPersistentDataContainer
     */
    @Override
    public TextComponent toJson() {
        final PersistentDataContainer container = getPersistentDataContainer();

        if (container == null) return Component.text("{\n}").color(NamedTextColor.WHITE);

        return new DataContainerCompound(container).toJson();
    }

    /**
     * すべてのキーを取得します。
     * @return キー文字列の配列
     */
    @Override
    public String[] getAllKeys() {
        return new DataContainerCompound(getPersistentDataContainer()).getAllKeys();
    }
}
