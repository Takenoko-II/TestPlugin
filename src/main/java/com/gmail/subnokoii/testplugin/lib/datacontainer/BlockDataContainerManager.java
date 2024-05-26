package com.gmail.subnokoii.testplugin.lib.datacontainer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class BlockDataContainerManager extends DataContainerManager {
    private final Block block;

    public BlockDataContainerManager(Block block) {
        this.block = block;
    }

    /**
     * 指定パスのデータを取得します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param type データ型
     * @return 指定のデータ型あるいはnull
     */
    @Override
    public @Nullable <P, C> C get(String path, PersistentDataType<P, C> type) {
        if (!(block.getState() instanceof TileState)) {
            return null;
        }

        final TileState tileState = (TileState) block.getState();

        return new DataContainerAccessor(tileState.getPersistentDataContainer()).get(path, type);
    }

    /**
     * 指定パスにカスタムデータをセットします。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value セットする値
     * @return 値がセットされたアイテム
     */
    @Override
    public BlockDataContainerManager set(String path, Object value) {
        if (!(block.getState() instanceof TileState)) {
            return this;
        }

        final TileState tileState = (TileState) block.getState();

        new DataContainerAccessor(tileState.getPersistentDataContainer()).set(path, value);

        return this;
    }

    /**
     * 指定パスのカスタムデータを削除します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return 値が削除されたアイテム
     */
    @Override
    public BlockDataContainerManager delete(String path) {
        if (!(block.getState() instanceof TileState)) {
            return this;
        }

        final TileState tileState = (TileState) block.getState();

        new DataContainerAccessor(tileState.getPersistentDataContainer()).delete(path);

        return this;
    }

    /**
     * 指定のパスのカスタムデータを持っているかを確認します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @return パスが存在すれば真
     */
    @Override
    public boolean has(String path) {
        if (!(block.getState() instanceof TileState)) {
            return false;
        }

        final TileState tileState = (TileState) block.getState();

        return new DataContainerAccessor(tileState.getPersistentDataContainer()).has(path);
    }

    /**
     * 指定パスのカスタムデータを取得し、別の値と比較します。
     * @param path 名前空間を省略したドット区切りのNBTパス
     * @param value 比較対象
     * @return 等しい値であれば真
     */
    @Override
    public boolean equals(String path, Object value) {
        if (!(block.getState() instanceof TileState)) {
            return false;
        }

        final TileState tileState = (TileState) block.getState();

        return new DataContainerAccessor(tileState.getPersistentDataContainer()).equals(path, value);
    }

    /**
     * データをJSONに変換します。
     * @return JSON化されたPersistentDataContainer
     */
    @Override
    public Component toJson() {
        if (!(block.getState() instanceof TileState)) {
            return Component.text("{\n}").color(NamedTextColor.WHITE);
        }

        final TileState tileState = (TileState) block.getState();

        return new DataContainerAccessor(tileState.getPersistentDataContainer()).toJson();
    }
}
