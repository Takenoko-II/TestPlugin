package com.gmail.subnokoii78.util.itemstack.components;

import org.jetbrains.annotations.NotNull;
public interface ItemStackComponent {
    /**
     * このコンポーネントが有効になっているかどうかを調べます。
     * @return 有効であれば真
     */
    boolean isEnabled();

    /**
     * このコンポーネントを無効化します。
     */
    void disable();

    /**
     * このコンポーネントのIDを返します。
     * @return コンポーネントID
     */
    @NotNull String getComponentId();
}
