package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.groups.PlayerGroup;
import dev.polv.polcinematics.net.Packets;
import dev.polv.polcinematics.utils.CommandUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class PlayerSubcommand {

    
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> controlArgumentBuilder = CommandManager.literal("control");

        controlArgumentBuilder.then(CommandManager.literal("broadcast")
                .then(
                        CommandUtils.arg_cinematic()
                        .executes(PlayerSubcommand::broadcast)
                )
        );

        controlArgumentBuilder.then(CommandManager.literal("unbroadcast")
                .then(
                        CommandUtils.arg_cinematic()
                                .executes(PlayerSubcommand::unbroadcast)
                )
        );

        controlArgumentBuilder.then(
                CommandManager.literal("play")
                        .then(
                                CommandManager.literal("all")
                                        .then(
                                                CommandUtils.arg_cinematic()
                                                        .then(
                                                                CommandUtils.arg_from()
                                                                        .then(
                                                                                CommandUtils.arg_paused()
                                                                                        .executes(PlayerSubcommand::play)
                                                                        )
                                                                        .executes(PlayerSubcommand::play)
                                                        )
                                                        .executes(PlayerSubcommand::play)
                                        )
                                        .executes(PlayerSubcommand::play)
                        )
                        .then(
                                CommandManager.literal("group")
                                        .then(
                                                CommandUtils.arg_group()
                                                        .then(
                                                                CommandUtils.arg_cinematic()
                                                                        .then(
                                                                                CommandUtils.arg_from()
                                                                                        .then(
                                                                                                CommandUtils.arg_paused()
                                                                                                        .executes(PlayerSubcommand::play)
                                                                                        )
                                                                                        .executes(PlayerSubcommand::play)
                                                                        )
                                                                        .executes(PlayerSubcommand::play)
                                                        )
                                        )
                        )
        );

        controlArgumentBuilder.then(
                CommandManager.literal("stop")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(PlayerSubcommand::stop)
                        )
        );

        controlArgumentBuilder.then(
                CommandManager.literal("pause")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(PlayerSubcommand::pause)
                        )
        );

        controlArgumentBuilder.then(
                CommandManager.literal("goto")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .then(
                                                CommandUtils.arg_to()
                                                        .executes(PlayerSubcommand::gotocmd)
                                        )
                        )
        );

        controlArgumentBuilder.then(
                CommandManager.literal("resume")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(PlayerSubcommand::resume)
                        )
        );

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
        } catch (NumberFormatException e) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cInvalid start time"));
            return 1;
        } catch (Exception ignore) {}

        PlayerGroup group = null;
        try {
            group = PolCinematics.getGroupManager().getGroup(context.getArgument("group", String.class));
            if (group == null) {
                context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cGroup not found"));
                return 1;
            }
        } catch (Exception ignore) {}

        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        paused = paused == null ? false : paused;
        from = from == null ? 0 : from;

        List<ServerPlayerEntity> players = context.getSource().getServer().getPlayerManager().getPlayerList();
        if (group != null) {
            players = group.getPlayers(context.getSource());
        }

        Packets.sendCinematicPlay(players, cinematic.getUuid(), paused, from);
        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Playing cinematic §f" + cinematic.getName() + " §7with start time §f" + from + " §7and paused §f" + paused));
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        Packets.sendCinematicStop(context.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Stopping cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int broadcast(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        if (PolCinematics.CINEMATICS_MANAGER.isCinematicBroadcasted(cinematic)) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + cinematic.getName() + " §cis already broadcasted"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.addBroadcastedCinematic(cinematic);
        Packets.broadcastCinematic(cinematic, context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Broadcasting cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int unbroadcast(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        if (!PolCinematics.CINEMATICS_MANAGER.isCinematicBroadcasted(cinematic)) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + cinematic.getName() + " §cis not broadcasted"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.removeBroadcastedCinematic(cinematic);
        Packets.unbroadcastCinematic(cinematic.getUuid(), context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Unbroadcasting cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        Packets.sendCinematicPause(context.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Pausing cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);

        Packets.sendCinematicResume(context.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Resuming cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int gotocmd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(context, false);
        long to = context.getArgument("to", Long.class);

        Packets.sendCinematicGoto(context.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid(), to);

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Moving cinematic §f" + cinematic.getName() + " §7to §f" + to));
        return 1;
    }

}
