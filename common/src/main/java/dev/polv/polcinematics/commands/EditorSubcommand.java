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
        builder.then(CommandManager.literal("start").executes(EditorSubcommand::start));
        builder.then(CommandManager.literal("stop").executes(EditorSubcommand::stop));
        builder.then(CommandManager.literal("open").executes(EditorSubcommand::open));

        return builder.build();
    }

    private static int start(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendMessage(Text.of("Editor server already running"));
        } else {
            FlutterGuiManager.INSTANCE.startServer();
            context.getSource().sendMessage(Text.of("Starting server..."));
        }
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendMessage(Text.of("Stopping server..."));
            FlutterGuiManager.INSTANCE.stopServer();
        } else {
            context.getSource().sendMessage(Text.of("Server is not running"));
        }
        return 1;
    }

    private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!FlutterGuiManager.INSTANCE.isRunning()) {
            context.getSource().sendMessage(Text.of("Server has not stated"));
            return 1;
        }

        Packets.sendOpenServer(context.getSource().getPlayer());
        return 1;
    }

}
