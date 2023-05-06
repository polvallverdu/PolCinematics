package dev.polv.polcinematics.utils;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.attributes.Attribute;
import dev.polv.polcinematics.cinematic.compositions.attributes.Keyframe;
import dev.polv.polcinematics.cinematic.compositions.value.Value;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.subcommands.ManagerSubcommand;
import net.minecraft.command.CommandException;
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

    private static Cinematic getSelectedCinematic(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        Cinematic cinematic;
        if ((cinematic = PolCinematics.CINEMATICS_MANAGER.getSelectedCinematic(player)) != null) {
            return cinematic;
        }
        return null;
    }

    public static Pair<Cinematic, Timeline> getTimeline(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(context);

        String timelineName = StringArgumentType.getString(context, "timeline");
        Timeline timeline = cinematic.resolveTimeline(timelineName);

        if (timeline == null) {
            throw PolCinematicsCommand.INVALID_TIMELINE.create();
        }

        return new Pair<>(cinematic, timeline);
    }

    public static Pair<Timeline, WrappedComposition> getComposition(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(context);

        String compoQuery = StringArgumentType.getString(context, "composition");
        var pair = cinematic.getTimelineAndWrappedComposition(compoQuery);

        if (pair == null)
            throw PolCinematicsCommand.INVALID_COMPOSITION.create();

        return pair;
    }

    public static Pair<String, Value> getProperty(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var pairtc = getComposition(context);
        String propertyKey = StringArgumentType.getString(context, "property");

        Value value = pairtc.getRight().getComposition().getProperty(propertyKey);

        if (value == null)
            throw PolCinematicsCommand.INVALID_PROPERTY.create();

        return new Pair<>(propertyKey, value);
    }

    public static Pair<String, Attribute> getAttribute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var pairtc = getComposition(context);
        String attributeKey = StringArgumentType.getString(context, "attribute");

        Attribute attr = pairtc.getRight().getComposition().getAttribute(attributeKey);

        if (attr == null)
            throw PolCinematicsCommand.INVALID_ATTRIBUTE.create();

        return new Pair<>(attributeKey, attr);
    }

    public static Pair<Long, Keyframe> getKeyframe(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var pairkattr = getAttribute(context);
        long time = LongArgumentType.getLong(context, "time");

        Keyframe keyframe = pairkattr.getRight().getExactKeyframe(LongArgumentType.getLong(context, "time"));

        if (keyframe == null)
            throw PolCinematicsCommand.INVALID_KEYFRAME.create();

        return new Pair<>(time, keyframe);
    }

}
