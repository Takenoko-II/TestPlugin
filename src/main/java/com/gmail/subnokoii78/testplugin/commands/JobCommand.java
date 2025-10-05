package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.system.Job;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.gmail.subnokoii78.tplcore.commands.arguments.AbstractEnumerationArgument;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class JobCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("job")
            .then(
                Commands.literal("get")
                    .then(
                        Commands.argument("player", ArgumentTypes.player())
                            .executes(this::getJob)
                    )
            )
            .then(
                Commands.literal("set")
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .then(
                                Commands.argument("job", new JobArgument())
                                    .executes(this::setJob)
                            )
                    )
            )
            .then(
                Commands.literal("take")
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes(this::takeJob)
                    )
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "職業を操作します";
    }

    private int getJob(CommandContext<CommandSourceStack> context) {
        final List<Player> players;
        try {
            players = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(context.getSource(), e);
        }

        if (players.isEmpty()) {
            return failure(context.getSource(), new IllegalStateException(
                "セレクターを満たすプレイヤーが見つかりませんでした"
            ));
        }

        final Player player = players.getFirst();

        final Job job;
        try {
            job = Job.getJob(player);
        }
        catch (Exception e) {
            return failure(context.getSource(), e);
        }

        context.getSource().getSender().sendMessage(Component.text(
            "プレイヤー '" + player.getName() + "' の職業は '" + job.getId() + "' です"
        ));

        return Command.SINGLE_SUCCESS;
    }

    private int setJob(CommandContext<CommandSourceStack> context) {
        final List<Player> players;
        try {
            players = context.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(context.getSource(), e);
        }

        if (players.isEmpty()) {
            return failure(context.getSource(), new IllegalStateException(
                "セレクターを満たすプレイヤーが見つかりませんでした"
            ));
        }

        final Job job = context.getArgument("job", Job.class);

        int c = 0;
        for (final Player player : players) {
            if (!(Job.hasJob(player) && Job.getJob(player) == job)) {
                Job.setJob(player, job);
                c++;
            }
        }

        if (c == 0) {
            return failure(context.getSource(), new IllegalStateException(
                "セレクターを満たすすべてのプレイヤーは職業 '" + job.getId() + "' を既に持っています"
            ));
        }

        context.getSource().getSender().sendMessage(Component.text(
             c + "人のプレイヤーの職業を '" + job.getId() + "' に設定しました"
        ));

        return Command.SINGLE_SUCCESS;
    }

    private int takeJob(CommandContext<CommandSourceStack> context) {
        final List<Player> players;
        try {
            players = context.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(context.getSource(), e);
        }

        if (players.isEmpty()) {
            return failure(context.getSource(), new IllegalStateException(
                "セレクターを満たすプレイヤーが見つかりませんでした"
            ));
        }

        int c = 0;
        for (final Player player : players) {
            if (Job.hasJob(player)) c++;
            Job.takeJob(player);
        }

        if (c == 0) {
            return failure(context.getSource(), new IllegalStateException(
                "職業を持ったプレイヤーが見つかりませんでした"
            ));
        }

        context.getSource().getSender().sendMessage(Component.text(
            c + "人のプレイヤーの職業をリセットしました"
        ));

        return Command.SINGLE_SUCCESS;
    }

    public static final class JobArgument extends AbstractEnumerationArgument<Job> {

        @Override
        protected Class<Job> getEnumClass() {
            return Job.class;
        }

        @Override
        protected String getErrorMessage(String s) {
            return "無効な職業IDです: " + s;
        }
    }

    public static final JobCommand JOB_COMMAND = new JobCommand();
}
