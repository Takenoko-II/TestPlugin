package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class TooltipShowable extends ItemStackComponent {
    protected TooltipShowable(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    /**
     * このコンポーネントがツールチップに表示されるかどうかを返します。
     * @return 表示されるなら真
     */
    public abstract boolean getShowInTooltip();

    /**
     * このコンポーネントをツールチップに表示するかどうかを変更します。
     * @param flag 真であれば表示
     */
     abstract void setShowInTooltip(boolean flag);
}
