package com.gmail.subnokoii78.util.datacontainer;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class EntityDataContainerManager extends DataContainerManager {
    private final Entity entity;

    /**
     * エンティティ内のカスタムデータを操作するためのオブジェクトを作成します。
     * @param entity 使用するエンティティ
     */
    public EntityDataContainerManager(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entityが必要ですがnullが渡されました");
        }

        this.entity = entity;
    }

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        return new DataContainerCompound(entity.getPersistentDataContainer()).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    @Override
    public EntityDataContainerManager set(String path, Object value) {
        new DataContainerCompound(entity.getPersistentDataContainer()).set(path, value);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    @Override
    public EntityDataContainerManager delete(String path) {
        new DataContainerCompound(entity.getPersistentDataContainer()).delete(path);

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    @Override
    public boolean has(String path) {
        return new DataContainerCompound(entity.getPersistentDataContainer()).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    @Override
    public boolean equals(String path, Object value) {
        return new DataContainerCompound(entity.getPersistentDataContainer()).equals(path, value);
    }

    /**
     * データをJSONに変換します。
     *
     * @return JSON化されたPersistentDataContainer
     */
    @Override
    public TextComponent toJson() {
        return new DataContainerCompound(entity.getPersistentDataContainer()).toJson();
    }

    /**
     * すべてのキーを取得します。
     * @return キー文字列の配列
     */
    @Override
    public String[] getAllKeys() {
        return new DataContainerCompound(entity.getPersistentDataContainer()).getAllKeys();
    }
}
