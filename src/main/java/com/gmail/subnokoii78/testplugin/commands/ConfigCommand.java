package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONSerializer;
import com.gmail.takenokoii78.json.JSONValue;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConfigCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("config")
            .requires(stack -> {
                return stack.getSender().isOp();
            })
            .then(
                Commands.literal("get")
                    .then(
                        Commands.argument("path", StringArgumentType.string())
                            .executes(this::getWithPath)
                    )
                    .executes(this::getRoot)
            )
            .then(
                Commands.literal("reload")
                    .executes(this::reload)
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "コンフィグファイルを操作するコマンド";
    }

    private int getWithPath(CommandContext<CommandSourceStack> context) {
        final JSONPath path = JSONPath.of(context.getArgument("path", String.class));
        final JSONObject object = TPLCore.getPluginConfigLoader().get();
        if (object.has(path)) {
            final JSONValue<?> value = object.get(path, object.getTypeOf(path));

            final String s;
            if (value instanceof JSONStructure structure) {
                s = JSONSerializer.serialize(structure);
            }
            else {
                s = value.toString();
            }

            context.getSource().getSender().sendMessage("パス " + path + " の値を取得しました: " + s);

            return Command.SINGLE_SUCCESS;
        }
        else {
            return failure(context.getSource(), new IllegalArgumentException(
                "パスが存在しません: " + path
            ));
        }
    }

    private int getRoot(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage("ルートを取得しました: " + JSONSerializer.serialize(
            TPLCore.getPluginConfigLoader().get()
        ));
        return Command.SINGLE_SUCCESS;
    }

    private int reload(CommandContext<CommandSourceStack> context) {
        if (TPLCore.getPluginConfigLoader().reload()) {
            // コマンドの実行権限更新
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);

            context.getSource().getSender().sendMessage("再読み込みに成功しました");
            return Command.SINGLE_SUCCESS;
        }
        else {
            return failure(context.getSource(), new IllegalStateException(
                "再読み込みに失敗しました: 代わりにデフォルトのファイルを使用します"
            ));
        }
    }

    public static final ConfigCommand CONFIG_COMMAND = new ConfigCommand();
}
