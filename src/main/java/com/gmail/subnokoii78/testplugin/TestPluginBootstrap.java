package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.tplcore.TPLCore;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DiscoveredDatapack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class TestPluginBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        final URI uri;
        try {
            uri = getClass().getResource(DATAPACK_PATH).toURI();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            final DiscoveredDatapack datapack;
            try {
                datapack = event.registrar().discoverPack(uri, DATAPACK_ID);
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }

            if (datapack == null) {
                System.out.println("Load Failure: Could not get discovered datapack: " + DATAPACK_ID + " is null");
            }
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new TestPlugin(this);
    }

    public @NotNull Datapack getDatapack() {
        final Datapack datapack = Bukkit.getDatapackManager().getPack(TPLCore.getPlugin().getName() + '/' + TestPluginBootstrap.DATAPACK_ID);

        if (datapack == null) {
            throw new IllegalStateException("データパック '" + DATAPACK_ID + "' の取得に失敗しました");
        }

        return datapack;
    }

    public static final String DATAPACK_PATH = "/datapack";

    public static final String DATAPACK_ID = "plugin_api";
}
