package dev.polv.polcinematics.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EditorSubcommand {

    public static LiteralCommandNode<ServerCommandSource> register(LiteralArgumentBuilder<ServerCommandSource> builder, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal("ceditor");

        builder.then(CommandManager.literal("start").executes(EditorSubcommand::start));
        builder.then(CommandManager.literal("stop").executes(EditorSubcommand::stop));
        builder.then(CommandManager.literal("open").executes(EditorSubcommand::open));

        return builder.build();
    }

    private static int start(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendFeedback(Text.of("Editor server already running"), false);
        } else {
            FlutterGuiManager.INSTANCE.startServer();
            context.getSource().sendFeedback(Text.of("Starting server..."), false);
        }
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendFeedback(Text.of("Stopping server..."), false);
            FlutterGuiManager.INSTANCE.stopServer();
        } else {
            context.getSource().sendFeedback(Text.of("Server is not running"), false);
        }
        return 1;
    }

    private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendFeedback(Text.of("Server has not stated"), false);
            return 1;
        }

        Packets.sendOpenServer(context.getSource().getPlayer());
        return 1;
    }

}
