package com.gmail.subnokoii.testplugin.lib.itemstack.components;

public interface ItemStackComponent {
    boolean getEnabled();

    void disable();

    boolean getShowInTooltip(boolean flag);

    void setShowInTooltip(boolean flag);
}
