package com.gmail.subnokoii78.testplugin.commands.brigadier;

import com.gmail.subnokoii78.testplugin.PluginDirectoryManager;
import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.file.TextFileUtils;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONSerializer;
import com.gmail.subnokoii78.util.other.CalcExpEvalException;
import com.gmail.subnokoii78.util.other.CalcExpEvaluator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

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

    RELOAD_CONFIG {
        @Override
        public LiteralCommandNode<CommandSourceStack> getNode() {
            return Commands.literal("config")
                .then(Commands.literal("reload").executes(ctx -> {
                    PluginDirectoryManager.reloadConfig();
                    ctx.getSource().getSender().sendPlainMessage(TestPlugin.CONFIG_FILE_PATH + "をリロードしました");
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("get").executes(ctx -> {
                    final JSONObject jsonObject = PluginDirectoryManager.getConfig();
                    ctx.getSource().getSender().sendMessage(new JSONSerializer(jsonObject).serialize());
                    return Command.SINGLE_SUCCESS;
                }))
                .build();
        }
    },

    LOGGER {
        private Command<CommandSourceStack> getLogReader(int start, int end, boolean fromLast) {
            return ctx -> {
                final var log = TextFileUtils.read("logs/latest.log");
                final var sender = ctx.getSource().getSender();
                var component = fromLast
                    ? Component.text("ログの終わり" + (end - start + 1) + "行を表示しています")
                    : Component.text("ログの初め" + (end - start + 1) + "行を表示しています");

                final var sub = fromLast
                    ? log.subList(log.size() - (end + 1), log.size() - (start + 1))
                    : log.subList(start, end);

                for (int i = 0; i < sub.size(); i++) {
                    final var line = sub.get(i);
                    if (i % 2 == 0) {
                        component = component.append(Component.text("\n- ").color(NamedTextColor.BLUE)).append(Component.text(line).color(NamedTextColor.WHITE));
                    }
                    else {
                        component = component.append(Component.text("\n- ").color(NamedTextColor.BLUE)).append(Component.text(line).color(NamedTextColor.GRAY));
                    }
                }
                sender.sendMessage(component);
                return Command.SINGLE_SUCCESS;
            };
        }

        @Override
        public LiteralCommandNode<CommandSourceStack> getNode() {
            return Commands.literal("log")
                .then(
                    Commands.literal("read")
                        .then(Commands.literal("first").executes(getLogReader(0, 31, false)))
                        .then(Commands.literal("last").executes(getLogReader(0, 31, true)))
                        .executes(getLogReader(0, 15, true))
                )
                .then(
                    Commands.literal("write")
                        .then(Commands.argument("message", ArgumentTypes.component())
                            .executes(ctx -> {
                                final var message = ctx.getArgument("message", Component.class);
                                TestPlugin.getInstance().getComponentLogger().info(message);
                                ctx.getSource().getSender().sendMessage(Component.text("メッセージをログに書き込みました: ").append(message));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                )
                .then(Commands.literal("clear").executes(ctx -> {
                    Arrays.stream(TextFileUtils.getAll("logs"))
                        .filter(path -> path.endsWith(".log.gz"))
                        .forEach(TextFileUtils::delete);
                    ctx.getSource().getSender().sendPlainMessage("ログのアーカイブを全て削除しました");
                    return Command.SINGLE_SUCCESS;
                }))
                .build();
        }
    },

    EVALUATE {
        @Override
        public LiteralCommandNode<CommandSourceStack> getNode() {
            return Commands.literal("evaluate")
                .then(Commands.argument("expression", ArgumentTypes.signedMessage()).executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    final String expression = ctx.getArgument("expression", SignedMessageResolver.class).content();
                    final CalcExpEvaluator evaluator = CalcExpEvaluator.getDefaultEvaluator();

                    try {
                        final double result = evaluator.evaluate(expression);
                        sender.sendMessage(Component.text("演算結果: ").append(Component.text(result)));
                        return (int) result;
                    }
                    catch (CalcExpEvalException e) {
                        sender.sendMessage(
                            Component.text(e.getMessage() == null ? "式の評価に失敗しました" : ("式の評価に失敗しました: " + e.getMessage()))
                                .color(NamedTextColor.RED)
                                .hoverEvent(HoverEvent.showText(Component.text("クリックして例外を投げる").color(NamedTextColor.RED)))
                                .clickEvent(ClickEvent.callback(audience -> {
                                    sender.sendMessage("発生した例外を投げます...");
                                    throw e;
                                }))
                        );

                        return 0;
                    }
                }))
                .build();
        }
    };

    public abstract LiteralCommandNode<CommandSourceStack> getNode();
}
