package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.util.itemstack.ItemStackBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class BungeeCordUtils {
    private BungeeCordUtils() {}

    public static ItemStack getServerSelector() {
        return new ItemStackBuilder(Material.COMPASS)
            .customName("Server Selector", NamedTextColor.GREEN)
            .lore("Right Click to Open", Color.GRAY)
            .glint(true)
            .maxCount(1)
            .dataContainer("locked", true)
            .dataContainer("custom_item_tag", "server_selector")
            .build();
    }

    public static void transfer(Player player, ServerType serverType) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverType.name);
        final byte[] data = output.toByteArray();

        player.sendPluginMessage(TestPlugin.getInstance(), "BungeeCord", data);
    }

    public enum ServerType {
        LOBBY("lobby"),

        GAME("game"),

        DEVELOPMENT("develop");

        private final String name;

        ServerType(String name) {
            this.name = name;
        }

        public static @Nullable ServerType from(String name) {
            for (final ServerType value : ServerType.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }

            return null;
        }
    }
}