package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.compositions.values.constants.Constant;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.TimeVariable;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class CinematicThingsSuggestion implements SuggestionProvider<ServerCommandSource> {

    public enum SuggestionType {
        TIMELINE,
        COMPOSITION,
        CONSTANT_KEYS,
        CONSTANT_VALUE,
        TIMEVARIABLE_KEYS,
        TIMEVARIABLE_VALUE,
        TIMEVARIABLE_POSITION,
    }

    private final SuggestionType type;

    public CinematicThingsSuggestion(SuggestionType type) {
        this.type = type;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        if (player == null) {
            return Suggestions.empty();
        }

        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getSelectedCinematic(player);

        if (cinematic == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.TIMELINE) {
            builder.suggest("camera");
            builder.suggest(cinematic.getCameraTimeline().getUuid().toString(), Text.of("Timeline: camera"));
            for (int i = 0; i < cinematic.getTimelineCount(); i++) {
                String timelineName = String.valueOf(i+1);
                builder.suggest(timelineName);
                builder.suggest(cinematic.getTimeline(i).getUuid().toString(), Text.of("Timeline: " + timelineName));
            }

            return builder.buildFuture();
        }

        String timelinename = StringArgumentType.getString(ctx, "timeline");
        Timeline timeline = cinematic.resolveTimeline(timelinename);

        if (timeline == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.COMPOSITION) {
            timeline.getWrappedCompositions().forEach(wc -> {
                builder.suggest(wc.getUuid().toString(), Text.of(wc.getComposition().getName()));
                builder.suggest(wc.getComposition().getName());
            });

            return builder.buildFuture();
        }

        String compositionname = StringArgumentType.getString(ctx, "composition");
        WrappedComposition wrappedComposition = timeline.findWrappedComposition(compositionname);

        if (wrappedComposition == null) {
            return Suggestions.empty();
        }

        Composition composition = wrappedComposition.getComposition();

        if (type == SuggestionType.TIMEVARIABLE_KEYS) {
            composition.getCompositionTimeVariables().getKeys().forEach(builder::suggest);
            return builder.buildFuture();
        }

        if (type == SuggestionType.CONSTANT_KEYS) {
            composition.getCompositionConstants().getKeys().forEach(builder::suggest);
            return builder.buildFuture();
        }

        if (type == SuggestionType.TIMEVARIABLE_POSITION) {
            composition.getCompositionTimeVariables().getTimeVariables().forEach(timeVariable -> {
                timeVariable.getAllKeyframes().forEach(kf -> builder.suggest(String.valueOf(kf.getTime())));
            });
            return builder.buildFuture();
        }

        if (type == SuggestionType.CONSTANT_VALUE || type == SuggestionType.TIMEVARIABLE_VALUE) {
            EValueType valueType = null;

            if (type == SuggestionType.CONSTANT_VALUE) {
                String constantValue = StringArgumentType.getString(ctx, "constant");
                Constant val = composition.getConstant(constantValue);
                if (val == null) {
                    return Suggestions.empty();
                }

                valueType = val.getType();
            }

            if (type == SuggestionType.TIMEVARIABLE_VALUE) {
                String timeVariableKey = StringArgumentType.getString(ctx, "timevariable");
                TimeVariable attr = composition.getTimeVariable(timeVariableKey);
                if (attr == null) {
                    return Suggestions.empty();
                }

                valueType = attr.getType();
            }

            switch (valueType) {
                case CAMERAROT, COLOR, DOUBLE, STRING, INTEGER -> {
                    return Suggestions.empty();
                }
                case CAMERAPOS -> {
                    return Vec3ArgumentType.vec3().listSuggestions(ctx, builder);
                }
                case BOOLEAN -> {
                    builder.suggest("true");
                    builder.suggest("false");
                }
            }

            return builder.buildFuture();
        }

        return Suggestions.empty();
    }

}
