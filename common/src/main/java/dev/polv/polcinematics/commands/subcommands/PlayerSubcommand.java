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

    private static int play(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Boolean paused = false;
        try {
            paused = ctx.getArgument("paused", Boolean.class);
        } catch (Exception ignore) {}

        Long from = 0L;
        try {
            from = ctx.getArgument("from", Long.class);
        } catch (NumberFormatException e) {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cInvalid start time"));
            return 1;
        } catch (Exception ignore) {}

        PlayerGroup group = null;
        try {
            group = PolCinematics.getGroupManager().getGroup(ctx.getArgument("group", String.class));
            if (group == null) {
                ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cGroup not found"));
                return 1;
            }
        } catch (Exception ignore) {}

        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        paused = paused == null ? false : paused;
        from = from == null ? 0 : from;

        List<ServerPlayerEntity> players = ctx.getSource().getServer().getPlayerManager().getPlayerList();
        if (group != null) {
            players = group.getPlayers(ctx.getSource());
        }

        Packets.sendCinematicPlay(players, cinematic.getUuid(), paused, from);
        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Playing cinematic §f" + cinematic.getName() + " §7with start time §f" + from + " §7and paused §f" + paused));
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        Packets.sendCinematicStop(ctx.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Stopping cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int broadcast(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        if (PolCinematics.CINEMATICS_MANAGER.isCinematicBroadcasted(cinematic)) {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + cinematic.getName() + " §cis already broadcasted"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.addBroadcastedCinematic(cinematic);
        Packets.broadcastCinematic(cinematic, ctx.getSource().getServer().getPlayerManager().getPlayerList());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Broadcasting cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int unbroadcast(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        if (!PolCinematics.CINEMATICS_MANAGER.isCinematicBroadcasted(cinematic)) {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + cinematic.getName() + " §cis not broadcasted"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.removeBroadcastedCinematic(cinematic);
        Packets.unbroadcastCinematic(cinematic.getUuid(), ctx.getSource().getServer().getPlayerManager().getPlayerList());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Unbroadcasting cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        Packets.sendCinematicPause(ctx.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Pausing cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int resume(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        Packets.sendCinematicResume(ctx.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid());

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Resuming cinematic §f" + cinematic.getName()));
        return 1;
    }

    private static int gotocmd(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);
        long to = ctx.getArgument("to", Long.class);

        Packets.sendCinematicGoto(ctx.getSource().getServer().getPlayerManager().getPlayerList(), cinematic.getUuid(), to);

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Moving cinematic §f" + cinematic.getName() + " §7to §f" + to));
        return 1;
    }

}
