package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSubcommand {

    private static final List<UUID> broadcastedCinematics = new ArrayList<>();

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> controlArgumentBuilder = CommandManager.literal("control");

        controlArgumentBuilder.then(CommandManager.literal("broadcast")
                .then(
                        CommandManager.argument("cinematicname", StringArgumentType.string())
                        .suggests(new CinematicLoadedSuggestion())
                        .executes(PlayerSubcommand::broadcast)
                )
        );
        controlArgumentBuilder.then(CommandManager.literal("update").executes(PlayerSubcommand::update));
        controlArgumentBuilder.then(
                CommandManager.literal("play")
                        .then(
                                CommandManager.argument("from", LongArgumentType.longArg(0))
                                        .then(
                                                CommandManager.argument("paused", BoolArgumentType.bool())
                                                        .executes(PlayerSubcommand::play)
                                        )
                        )
                        .executes(PlayerSubcommand::play)
        );
        controlArgumentBuilder.then(CommandManager.literal("stop").executes(PlayerSubcommand::stop));
        controlArgumentBuilder.then(CommandManager.literal("pause").executes(PlayerSubcommand::pause));
        controlArgumentBuilder.then(CommandManager.literal("goto").then(CommandManager.argument("to", LongArgumentType.longArg(0)).executes(PlayerSubcommand::gotocmd)));
        controlArgumentBuilder.then(CommandManager.literal("resume").executes(PlayerSubcommand::resume));

        return controlArgumentBuilder.build();
    }

    private static int play(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Boolean paused = false;
        try {
            paused = context.getArgument("paused", Boolean.class);
        } catch (Exception ignore) {}
        Long from = 0L;
        try {
            from = context.getArgument("from", Long.class);
        } catch (Exception ignore) {}

        paused = paused == null ? false : paused;
        from = from == null ? 0 : from;

        Packets.sendCinematicPlay(context.getSource().getServer().getPlayerManager().getPlayerList(), paused, from);
        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aPlaying cinematic"));
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicStop(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aStopping cinematic"));
        return 1;
    }

    private static int broadcast(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String cinematicname = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicname);

        if (cinematic == null) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + cinematicname + " §cnot found"));
            return 1;
        }

        broadcastedCinematics.add(cinematic.getUuid());
        Packets.broadcastCinematic(cinematic, context.getSource().getServer().getPlayerManager().getPlayerList());

        return 1;
    }

    private static int update(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO

        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicPause(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§ePausing cinematic"));
        return 1;
    }

    private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicResume(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§eResuming cinematic"));
        return 1;
    }

    private static int gotocmd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long to = context.getArgument("to", Long.class);
        Packets.sendCinematicGoto(context.getSource().getServer().getPlayerManager().getPlayerList(), to);

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§eMoving cinematic to: " + to));
        return 1;
    }

}
