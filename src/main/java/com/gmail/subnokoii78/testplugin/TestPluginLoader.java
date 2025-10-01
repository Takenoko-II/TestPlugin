package com.gmail.subnokoii78.testplugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.Set;

public class TestPluginLoader implements PluginLoader {
    public static final Set<String> LIBRARIES = Set.of(
        "org.codehaus.groovy:groovy:3.0.21",
        "org.xerial:sqlite-jdbc:3.46.0.0"
    );

    @Override
    public void classloader(PluginClasspathBuilder pluginClasspathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
            "central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        ).build());

        for (final String library : LIBRARIES) {
            resolver.addDependency(new Dependency(
                new DefaultArtifact(library), null
            ));
        }

        pluginClasspathBuilder.addLibrary(resolver);
    }
}
