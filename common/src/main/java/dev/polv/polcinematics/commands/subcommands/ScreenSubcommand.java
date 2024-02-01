package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.screen.ScreenTest;
import dev.polv.polcinematics.utils.CommandUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ScreenSubcommand {

    
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> controlArgumentBuilder = CommandUtils.l("screens");

        controlArgumentBuilder.then(CommandUtils.l("create")
                .executes(ScreenSubcommand::create)
        );

        return controlArgumentBuilder.build();
    }

    private static int create(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ScreenTest.create(ctx.getSource().getPlayer().getPos());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§ascreen"));
        return 1;
    }

}
