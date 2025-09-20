package com.gmail.subnokoii78.testplugin.commands.brigadier;

import com.gmail.subnokoii78.testplugin.PluginConfigurationManager;
import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.takenokoii78.json.JSONParseException;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONSerializer;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum BrigadierCommandNodes {
    FOO {
        public LiteralCommandNode<CommandSourceStack> getNode() {
            return Commands.literal("foo")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(Component.text("foo!"));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
        }
    },

    CONFIG {
        @Override
        public LiteralCommandNode<CommandSourceStack> getNode() {
            return Commands.literal("config")
                .requires(stack -> {
                    return stack.getSender().isOp();
                })
                .then(Commands.literal("reload").executes(ctx -> {
                    PluginConfigurationManager.reload();
                    ctx.getSource().getSender().sendPlainMessage(TestPlugin.CONFIG_FILE_PATH + "をリロードしました");
                    return Command.SINGLE_SUCCESS;
                }))
                .then(
                    Commands.literal("get")
                        .then(
                            Commands.argument("path", StringArgumentType.string())
                                .executes(ctx -> {
                                    final JSONObject jsonObject = PluginConfigurationManager.getRootObject();

                                    final JSONPath path;
                                    try {
                                        path = JSONPath.of(ctx.getArgument("path", String.class));
                                    }
                                    catch (JSONParseException e) {
                                        ctx.getSource().getSender().sendMessage(Component.text("無効な形式のパスです").color(NamedTextColor.RED));
                                        return 0;
                                    }

                                    if (jsonObject.has(path)) {
                                        final Object value = jsonObject.get(path, jsonObject.getTypeOf(path));
                                        if (value instanceof JSONStructure jsonValue) {
                                            ctx.getSource().getSender().sendMessage(JSONSerializer.serialize(jsonValue));
                                        }
                                        else {
                                            ctx.getSource().getSender().sendMessage(value.toString());
                                        }
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    else {
                                        ctx.getSource().getSender().sendMessage(Component.text("そのパスは存在しません").color(NamedTextColor.RED));
                                        return 0;
                                    }
                                })
                        )
                        .executes(ctx -> {
                            final JSONObject jsonObject = PluginConfigurationManager.getRootObject();
                            ctx.getSource().getSender().sendMessage(JSONSerializer.serialize(jsonObject));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(
                    Commands.literal("write")
                        .then(
                            Commands.argument("path", StringArgumentType.string())
                                .then(
                                    Commands.argument("value_b", BoolArgumentType.bool())
                                        .executes(getConfigWriter("value_b", Boolean.class))
                                )
                                .then(
                                    Commands.argument("value_d", DoubleArgumentType.doubleArg())
                                        .executes(getConfigWriter("value_d", Double.class))
                                )
                                .then(
                                    Commands.argument("value_s", StringArgumentType.string())
                                        .executes(getConfigWriter("value_s", String.class))
                                )
                        )
                )
                .build();
        }

        private <T> Command<CommandSourceStack> getConfigWriter(@NotNull String valueArgId, @NotNull Class<T> valueType) {
            return ctx -> {
                final JSONPath path;
                try {
                    path = JSONPath.of(ctx.getArgument("path", String.class));
                }
                catch (JSONParseException e) {
                    ctx.getSource().getSender().sendMessage(Component.text("無効な形式のパスです").color(NamedTextColor.RED));
                    return 0;
                }

                final T value = ctx.getArgument(valueArgId, valueType);

                PluginConfigurationManager.write(path, value, false);
                return Command.SINGLE_SUCCESS;
            };
        }
    };

    public abstract LiteralCommandNode<CommandSourceStack> getNode();
}
