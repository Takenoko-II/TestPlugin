package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
public abstract class ItemStackComponent {
    /**
     * このコンポーネントを保持する{@link ItemMeta}
     */
    protected final ItemMeta itemMeta;

    /**
     * {@link ItemMeta}をもとにこのコンポーネントを操作するインスタンスを作成します。
     * @param itemMeta このコンポーネントを保持するItemMeta
     */
    protected ItemStackComponent(@NotNull ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    /**
     * このコンポーネントが有効になっているかどうかを調べます。
     * @return 有効であれば真
     */
    public abstract boolean isEnabled();

    /**
     * このコンポーネントを無効化します。
     */
    public abstract void disable();

    /**
     * このコンポーネントのIDを返します。
     * @return コンポーネントID
     */
    public abstract @NotNull String getComponentId();
}
