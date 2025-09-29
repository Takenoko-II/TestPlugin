package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.takenokoii78.json.JSONFile;
import com.gmail.takenokoii78.json.JSONParseException;
import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.values.JSONObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

@NullMarked
public class PluginConfigLoader {
    private final String path;

    private final String defaultPath;

    private final JSONFile file;

    private final JSONObject defaultValues;

    private JSONObject values;

    public PluginConfigLoader(String path, String defaultPath) {
        this.path = path;
        this.defaultPath = defaultPath;
        file = new JSONFile(path);
        defaultValues = loadDefault();
        values = defaultValues;
        reload();
    }

    private JSONObject loadDefault() {
        final InputStream is = getClass().getResourceAsStream(defaultPath);

        if (is == null) {
            throw new IllegalStateException(defaultPath + " が見つかりませんでした");
        }

        final Stream<String> lines;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            lines = br.lines();
        }
        catch (IOException e) {
            throw new IllegalStateException("BufferedReaderを生成できませんでした", e);
        }

        final String defaultConfigJson = String.join("\n", lines.toList());

        final JSONObject d;
        try {
            d = JSONParser.object(defaultConfigJson);
        }
        catch (JSONParseException e) {
            throw new IllegalStateException(defaultPath + "の読み取りに失敗しました", e);
        }
        return d;
    }

    public void reload() {
        if (file.exists()) {
            JSONObject json;
            try {
                json = file.readAsObject();
            }
            catch (JSONParseException e) {
                TPLCore.getPlugin().getComponentLogger().info(
                    Component.text(path + "の読み取りに失敗しました: 代わりにデフォルトの設定ファイルを使用します")
                        .color(NamedTextColor.RED)
                );
                values = defaultValues;
                return;
            }

            final JSONObject defaultCpy = defaultValues.copy();
            defaultCpy.merge(json);
            values = defaultCpy;
        }
        else {
            file.create();
            file.write(defaultValues);
        }
    }

    public JSONObject get() {
        return values.copy();
    }
}
