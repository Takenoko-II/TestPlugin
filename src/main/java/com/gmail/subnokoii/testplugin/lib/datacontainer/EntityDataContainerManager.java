package com.gmail.subnokoii.testplugin.lib.datacontainer;

import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class EntityDataContainerManager extends DataContainerManager {
    private final Entity entity;

    public EntityDataContainerManager(Entity entity) {
        this.entity = entity;
    }

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    public @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        return new DataContainerAccessor(entity.getPersistentDataContainer()).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    @Override
    public EntityDataContainerManager set(String path, Object value) {
        new DataContainerAccessor(entity.getPersistentDataContainer()).set(path, value);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    @Override
    public EntityDataContainerManager delete(String path) {
        new DataContainerAccessor(entity.getPersistentDataContainer()).delete(path);

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    @Override
    public boolean has(String path) {
        return new DataContainerAccessor(entity.getPersistentDataContainer()).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    @Override
    public boolean equals(String path, Object value) {
        return new DataContainerAccessor(entity.getPersistentDataContainer()).equals(path, value);
    }
}
