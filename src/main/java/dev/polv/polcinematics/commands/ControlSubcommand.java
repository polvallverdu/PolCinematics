package dev.polv.polcinematics.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.commands.suggetions.CinematicNameSuggestion;
import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ControlSubcommand {

    public static LiteralCommandNode<ServerCommandSource> register(LiteralArgumentBuilder<ServerCommandSource> builder, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        //builder.then(CommandManager.literal("broadcast").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicNameSuggestion()).executes(ControlSubcommand::broadcast)));
        builder.then(
            CommandManager.literal("broadcast")
                .then(
                    CommandManager.argument("cinematicname", StringArgumentType.string())
                        .suggests(new CinematicNameSuggestion())
                        .then(
                            CommandManager.argument("paused", BoolArgumentType.bool())
                                .then(
                                    CommandManager.argument("from", LongArgumentType.longArg(0))
                                        .executes(ControlSubcommand::broadcast)
                                ).executes(ControlSubcommand::broadcast)
                        ).executes(ControlSubcommand::broadcast)
                )
        );
        builder.then(CommandManager.literal("play").then(CommandManager.argument("paused", BoolArgumentType.bool()).executes(ControlSubcommand::play)).then(CommandManager.argument("from", LongArgumentType.longArg(0)).executes(ControlSubcommand::play)));
        builder.then(CommandManager.literal("stop").executes(ControlSubcommand::stop));
        builder.then(CommandManager.literal("pause").executes(ControlSubcommand::pause));
        builder.then(CommandManager.literal("goto").then(CommandManager.argument("to", LongArgumentType.longArg(0)).executes(ControlSubcommand::gotocmd)));
        builder.then(CommandManager.literal("resume").executes(ControlSubcommand::resume));

        return builder.build();
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
        context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§aPlaying cinematic"), false);
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicStop(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§aStopping cinematic"), false);
        return 1;
    }

    private static int broadcast(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String cinematicname = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicname);

        if (cinematic == null) {
            context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§cCinematic §6" + cinematicname + " §cnot found"), false);
            return 1;
        }

        Packets.broadcastCinematic(cinematic, context.getSource().getServer().getPlayerManager().getPlayerList());

        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicPause(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§ePausing cinematic"), false);
        return 1;
    }

    private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicResume(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§eResuming cinematic"), false);
        return 1;
    }

    private static int gotocmd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long to = context.getArgument("to", Long.class);
        Packets.sendCinematicGoto(context.getSource().getServer().getPlayerManager().getPlayerList(), to);

        context.getSource().sendFeedback(Text.of(CinematicCommand.PREFIX + "§eMoving cinematic to: " + to), false);
        return 1;
    }

}
