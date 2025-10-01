package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class FooCommand extends AbstractCommand {

    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("foo")
            .executes(ctx -> {
                ctx.getSource().getSender().sendMessage("foo!");
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }

    @Override
    protected String getDescription() {
        return "foooooooooooooooooooooooo";
    }
}
