package dev.polv.polcinematics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

final public class AudioCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("caudio")
                .then(CommandManager.literal("resume").executes(AudioCommand::resume))
                .then(CommandManager.literal("stop").executes(AudioCommand::stop))
                .then(CommandManager.literal("jump").then(CommandManager.argument("time", LongArgumentType.longArg(0)).executes(AudioCommand::jump)))
                .then(CommandManager.literal("pause").executes(AudioCommand::pause))
                .then(CommandManager.literal("play").then(CommandManager.argument("path", StringArgumentType.string()).executes(AudioCommand::play)))
                .then(CommandManager.literal("volume").then(CommandManager.argument("volume", IntegerArgumentType.integer(0, 100)).executes(AudioCommand::volume)))
        );
    }

    private static List<ServerPlayerEntity> getAllPlayers(CommandContext<ServerCommandSource> context) {
        return context.getSource().getServer().getPlayerManager().getPlayerList();
    }

    private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerState(getAllPlayers(context), true);
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerStop(getAllPlayers(context));
        return 1;
    }

    private static int jump(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerSetTime(getAllPlayers(context), LongArgumentType.getLong(context, "time"));
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerState(getAllPlayers(context), false);
        return 1;
    }

    private static int play(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerCreate(getAllPlayers(context), StringArgumentType.getString(context, "path"), false, true);
        return 1;
    }

    /*private static int alpha(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        alpha = FloatArgumentType.getFloat(context, "alpha");
        return 1;
    }
*/
    private static int volume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendMediaPlayerSetVolume(getAllPlayers(context), LongArgumentType.getLong(context, "volume"));
        return 1;
    }


}
