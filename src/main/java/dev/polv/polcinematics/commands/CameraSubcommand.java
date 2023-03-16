package dev.polv.polcinematics.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.utils.math.Easing;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CameraSubcommand {

    private static Easing easing = Easing.EASE_INOUT_CUBIC;

    public static LiteralCommandNode<ServerCommandSource> register(LiteralArgumentBuilder<ServerCommandSource> builder, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        /*builder.then(CommandManager.literal("setPosition").then(CommandManager.argument("time", LongArgumentType.longArg(0)).executes(CameraSubcommand::setpos)));
        builder.then(CommandManager.literal("setHighlight").then(CommandManager.argument("time", LongArgumentType.longArg(0)).executes(CameraSubcommand::setHighlight)));

        builder.then(CommandManager.literal("setRotLock").then(CommandManager.argument("time", LongArgumentType.longArg(0)).then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(CameraSubcommand::setRotLock))));
        builder.then(CommandManager.literal("setPlayerRotLock").then(CommandManager.argument("time", LongArgumentType.longArg(0)).then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(CameraSubcommand::setPlayerRotLock))));
        builder.then(CommandManager.literal("setPlayerPosLock").then(CommandManager.argument("time", LongArgumentType.longArg(0)).then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(CameraSubcommand::setPlayerPosLock))));

        builder.then(CommandManager.literal("smooth").executes(CameraSubcommand::smooth));*/

        return builder.build();
    }

    /*private static int setpos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long time = context.getArgument("time", Long.class);

        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(time);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.X, time, player.getX(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.Y, time, player.getY(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.Z, time, player.getZ(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.YAW, time, player.getYaw(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.PITCH, time, player.getPitch(), easing);

        context.getSource().sendFeedback(Text.of("Set position at " + time), false);

        return 1;
    }

    private static int setHighlight(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long time = context.getArgument("time", Long.class);

        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(time);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.LOCKX, time, player.getX(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.LOCKY, time, player.getY(), easing);
        cameraCompo.addKeyframe(CameraComposition.CameraProperty.LOCKZ, time, player.getZ(), easing);

        return 1;
    }

    private static int setRotLock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long time = context.getArgument("time", Long.class);
        double value = context.getArgument("value", Double.class);

        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(time);

        cameraCompo.addKeyframe(CameraComposition.CameraProperty.LOCK_ROTATION, time, value, easing);

        return 1;
    }

    private static int setPlayerRotLock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long time = context.getArgument("time", Long.class);
        double value = context.getArgument("value", Double.class);

        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(time);

        cameraCompo.addKeyframe(CameraComposition.CameraProperty.PLAYER_ROTATION, time, value, easing);

        return 1;
    }

    private static int setPlayerPosLock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long time = context.getArgument("time", Long.class);
        double value = context.getArgument("value", Double.class);

        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(time);

        cameraCompo.addKeyframe(CameraComposition.CameraProperty.XYZ_PLAYER_LOCKED, time, value, easing);

        return 1;
    }

    private static int smooth(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 1;
        }

        Cinematic cinematic = CinematicCommand.getCinematic(player);
        CameraComposition cameraCompo = cinematic.checkCameraComposition(0);

        cameraCompo.smooth();
        return 1;
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("Player not found"));
            return null;
        }
        return player;
    }*/

}
