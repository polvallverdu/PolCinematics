package dev.polv.polcinematics.utils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.constantvariables.Constant;
import dev.polv.polcinematics.cinematic.compositions.timevariables.TimeVariable;
import dev.polv.polcinematics.cinematic.compositions.timevariables.Keyframe;
import dev.polv.polcinematics.cinematic.manager.FileCinematic;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.groups.PlayerGroup;
import dev.polv.polcinematics.commands.suggetions.CinematicFileSuggetion;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.commands.suggetions.GroupSuggestion;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;

import java.util.UUID;

public class CommandUtils {

    public static Cinematic getCinematic(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return getCinematic(ctx, true);
    }

    public static Cinematic getCinematic(CommandContext<ServerCommandSource> ctx, boolean checkSelected) throws CommandSyntaxException {
        Cinematic cinematic = null;

        try {
            String cinematicResolver = StringArgumentType.getString(ctx, "cinematic");
            cinematic = PolCinematics.CINEMATICS_MANAGER.resolveCinematic(cinematicResolver);
            if (cinematic == null) {
                throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
            }
            return cinematic;
        } catch (CommandSyntaxException e) {
            throw e;
        } catch (Exception ignore) {}

        if (checkSelected) {
            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

            cinematic = getSelectedCinematic(player);

            if (cinematic == null) {
                throw PolCinematicsCommand.CINEMATIC_NOT_SELECTED.create();
            }
        }

        return cinematic;
    }

    public static FileCinematic getFileCinematic(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        FileCinematic cinematic = null;
        String cinematicResolver = StringArgumentType.getString(ctx, "filename");
        cinematic = PolCinematics.CINEMATICS_MANAGER.resolveFileCinematic(cinematicResolver);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
        }

        return cinematic;
    }

    private static Cinematic getSelectedCinematic(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        Cinematic cinematic;
        if ((cinematic = PolCinematics.CINEMATICS_MANAGER.getSelectedCinematic(player)) != null) {
            return cinematic;
        }
        return null;
    }

    public static Pair<Cinematic, Timeline> getTimeline(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(ctx);

        String timelineName = StringArgumentType.getString(ctx, "timeline");
        Timeline timeline = cinematic.resolveTimeline(timelineName);

        if (timeline == null) {
            throw PolCinematicsCommand.INVALID_TIMELINE.create();
        }

        return new Pair<>(cinematic, timeline);
    }

    public static Pair<Timeline, WrappedComposition> getComposition(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(ctx);

        String compoQuery = StringArgumentType.getString(ctx, "composition");
        var pair = cinematic.getTimelineAndWrappedComposition(compoQuery);

        if (pair == null)
            throw PolCinematicsCommand.INVALID_COMPOSITION.create();

        return pair;
    }

    public static Pair<String, Constant> getConstant(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pairtc = getComposition(ctx);
        String constantKey = StringArgumentType.getString(ctx, "constant");

        Constant value = pairtc.getRight().getComposition().getConstant(constantKey);

        if (value == null)
            throw PolCinematicsCommand.INVALID_CONSTANT.create();

        return new Pair<>(constantKey, value);
    }

    public static Pair<String, TimeVariable> getTimeVariable(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pairtc = getComposition(ctx);
        String timeVariableKey = StringArgumentType.getString(ctx, "timevariable");

        TimeVariable attr = pairtc.getRight().getComposition().getTimeVariable(timeVariableKey);

        if (attr == null)
            throw PolCinematicsCommand.INVALID_TIMEVARIABLE.create();

        return new Pair<>(timeVariableKey, attr);
    }

    public static Pair<Long, Keyframe> getKeyframe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pairkattr = getTimeVariable(ctx);
        long time = LongArgumentType.getLong(ctx, "time");

        Keyframe keyframe = pairkattr.getRight().getExactKeyframe(LongArgumentType.getLong(ctx, "time"));

        if (keyframe == null)
            throw PolCinematicsCommand.INVALID_KEYFRAME.create();

        return new Pair<>(time, keyframe);
    }

    public static PlayerGroup getGroup(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String group = StringArgumentType.getString(ctx, "group");
        PlayerGroup playerGroup = PolCinematics.getGroupManager().resolveGroup(group);

        if (playerGroup == null) {
            throw PolCinematicsCommand.GROUP_NOT_FOUND.create();
        }

        return playerGroup;
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> arg_cinematic() {
        return CommandManager.argument("cinematic", StringArgumentType.word())
                .suggests(new CinematicLoadedSuggestion());
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> arg_filecinematic() {
        return CommandManager.argument("filename", StringArgumentType.string())
                .suggests(new CinematicFileSuggetion());
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> arg_group() {
        return CommandManager.argument("group", StringArgumentType.word())
                .suggests(new GroupSuggestion());
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> arg_selector() {
        return CommandManager.argument("selector", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> EntityArgumentType.players().listSuggestions(ctx, builder));
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Long> arg_from() {
        return CommandManager.argument("from", LongArgumentType.longArg(0));
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Long> arg_to() {
        return CommandManager.argument("to", LongArgumentType.longArg(0));
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Long> arg_time() {
        return CommandManager.argument("time", LongArgumentType.longArg(0));
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Boolean> arg_paused() {
        return CommandManager.argument("paused", BoolArgumentType.bool());
    }

}
