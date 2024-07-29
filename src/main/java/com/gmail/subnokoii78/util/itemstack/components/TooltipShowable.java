package com.gmail.subnokoii78.util.itemstack.components;

public interface TooltipShowable extends ItemStackComponent {
    /**
     * このコンポーネントがツールチップに表示されるかどうかを返します。
     * @return 表示されるなら真
     */
    boolean getShowInTooltip();

    /**
     * このコンポーネントをツールチップに表示するかどうかを変更します。
     * @param flag 真であれば表示
     */
    void setShowInTooltip(boolean flag);
}
