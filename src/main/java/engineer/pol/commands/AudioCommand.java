package engineer.pol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import engineer.pol.client.players.AudioPlayer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

final public class AudioCommand {

    private static AudioPlayer audioPlayer = null;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("audio")
                .then(CommandManager.literal("resume").executes(AudioCommand::resume))
                .then(CommandManager.literal("stop").executes(AudioCommand::stop))
                .then(CommandManager.literal("jump").then(CommandManager.argument("time", IntegerArgumentType.integer()).executes(AudioCommand::jump)))
                .then(CommandManager.literal("pause").executes(AudioCommand::pause))
                .then(CommandManager.literal("play").then(CommandManager.argument("path", StringArgumentType.string()).executes(AudioCommand::play))));
    }

    private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        audioPlayer.play();
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        audioPlayer.stop();
        return 1;
    }

    private static int jump(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        audioPlayer.setTime(context.getArgument("time", int.class));
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        audioPlayer.pause();
        return 1;
    }

    private static int play(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        audioPlayer = new AudioPlayer(context.getArgument("path", String.class));
        return 1;
    }


}
