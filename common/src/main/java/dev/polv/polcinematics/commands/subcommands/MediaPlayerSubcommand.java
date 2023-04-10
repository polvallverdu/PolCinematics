package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

final public class MediaPlayerSubcommand {

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> mediaPlayerBuilder = CommandManager.literal("mediaplayer");

        mediaPlayerBuilder.then(CommandManager.literal("resume").executes(MediaPlayerSubcommand::resume));
        mediaPlayerBuilder.then(CommandManager.literal("stop").executes(MediaPlayerSubcommand::stop));
        mediaPlayerBuilder.then(
                CommandManager
                        .literal("jump")
                        .then(
                                CommandManager
                                        .argument("time", LongArgumentType.longArg(0))
                                        .executes(MediaPlayerSubcommand::jump)
                        )
        );
        mediaPlayerBuilder.then(CommandManager.literal("pause").executes(MediaPlayerSubcommand::pause));
        mediaPlayerBuilder.then(
                CommandManager
                        .literal("play")
                        .then(
                                CommandManager
                                        .argument("path", StringArgumentType.string())
                                        .then(
                                                CommandManager.argument("paused", BoolArgumentType.bool())
                                                        .then(CommandManager
                                                                .argument("audioOnly", BoolArgumentType.bool())
                                                                .executes(MediaPlayerSubcommand::play)
                                                        )
                                                        .executes(MediaPlayerSubcommand::play)
                                        )
                                        .executes(MediaPlayerSubcommand::play)
                        )
        );
        mediaPlayerBuilder.then(
                CommandManager
                        .literal("volume")
                        .then(
                                CommandManager
                                        .argument("volume", IntegerArgumentType.integer(0, 100))
                                        .executes(MediaPlayerSubcommand::volume)
                        )
        );

        return mediaPlayerBuilder.build();
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
        boolean paused = false;
        boolean audioOnly = false;

        try {
            paused = BoolArgumentType.getBool(context, "paused");
        } catch (IllegalArgumentException ignored) {}
        try {
            audioOnly = BoolArgumentType.getBool(context, "audioOnly");
        } catch (IllegalArgumentException ignored) {}

        Packets.sendMediaPlayerCreate(getAllPlayers(context), StringArgumentType.getString(context, "path"), paused, audioOnly);
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
