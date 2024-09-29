package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.PlayerComboHandler;
import com.gmail.subnokoii78.util.event.DataPackMessageReceiveEvent;
import com.gmail.subnokoii78.util.event.PlayerLeftClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class CustomEventListener {
    public static final CustomEventListener INSTANCE = new CustomEventListener();

    private CustomEventListener() {}

    public void onLeftClick(PlayerLeftClickEvent event) {
        final PlayerComboHandler handler = PlayerComboHandler.getHandler(event.getPlayer());

        final ItemStack itemStack = event.getPlayer().getEquipment().getItem(EquipmentSlot.HAND);

        if (!itemStack.getType().equals(Material.IRON_SWORD)) return;

        if (PlayerLeftClickEvent.Action.ENTITY_HIT.equals(event.getAction())) {
            final Integer currentCount = handler.nextCombo();
            if (currentCount == null) {
                event.getPlayer().sendMessage(Component.text("CT中").color(NamedTextColor.GRAY));
                event.cancel();
            }
            else event.getPlayer().sendMessage(Component.text("段数: " + currentCount));
        }
        else {
            handler.stopCombo();
        }
    }

    public void onDataPackMessageReceive(DataPackMessageReceiveEvent event) {

    }
}
