package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.Timeline;
import dev.polv.polcinematics.internal.compositions.Composition;
import dev.polv.polcinematics.internal.compositions.values.EValueType;
import dev.polv.polcinematics.internal.compositions.values.constants.Constant;
import dev.polv.polcinematics.internal.compositions.values.timevariables.TimeVariable;
import dev.polv.polcinematics.internal.layers.Layer;
import dev.polv.polcinematics.internal.layers.WrappedComposition;
import dev.polv.polcinematics.utils.CameraUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class CinematicThingsSuggestion implements SuggestionProvider<ServerCommandSource> {

    public enum SuggestionType {
        LAYER,
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

        Timeline timeline = PolCinematics.CINEMATICS_MANAGER.getSelectedCinematic(player);

        if (timeline == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.LAYER) {
            for (int i = 0; i < timeline.getLayerCount(); i++) {
                String layerName = String.valueOf(i+1);
                builder.suggest(layerName, Text.of("UUID: " + timeline.getLayer(i).getUuid().toString()));
//                builder.suggest(cinematic.getLayer(i).getUuid().toString(), Text.of("layer: " + layerName));
            }

            if (timeline instanceof Cinematic cinematic) {
                builder.suggest("camera", Text.of("UUID: " + cinematic.getCameraLayer().getUuid().toString()));
//            builder.suggest(cinematic.getCameraLayer().getUuid().toString(), Text.of("layer: camera"));
            }

            return builder.buildFuture();
        }

        String layername = StringArgumentType.getString(ctx, "layer");
        Layer layer = timeline.resolveLayer(layername);

        if (layer == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.COMPOSITION) {
            layer.getWrappedCompositions().forEach(wc -> {
//                builder.suggest(wc.getUuid().toString(), Text.of(wc.getComposition().getName()));
                builder.suggest(wc.getComposition().getName(), Text.of("UUID: " + wc.getComposition().getUuid().toString()));
            });

            return builder.buildFuture();
        }

        String compositionname = StringArgumentType.getString(ctx, "composition");
        WrappedComposition wrappedComposition = layer.findWrappedComposition(compositionname);

        if (wrappedComposition == null) {
            return Suggestions.empty();
        }

        Composition composition = wrappedComposition.getComposition();

        if (type == SuggestionType.TIMEVARIABLE_KEYS) {
            if (CameraUtils.containsPositionArgument(composition)) {
                builder.suggest("POSITION");
            }
            composition.getCompositionTimeVariables().getKeys().forEach(builder::suggest);
            return builder.buildFuture();
        }

        if (type == SuggestionType.CONSTANT_KEYS) {
            if (CameraUtils.containsPositionArgument(composition)) {
                builder.suggest("POSITION");
            }
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
            boolean contant = type == SuggestionType.CONSTANT_VALUE;
            String key = StringArgumentType.getString(ctx, contant ? "constant" : "timevariable");
            EValueType valueType = null;

            if (CameraUtils.containsPositionArgument(composition) && key.equalsIgnoreCase("POSITION")) {
                return builder.buildFuture(); // Want autocomplete for position
            }

            if (type == SuggestionType.CONSTANT_VALUE) {
                Constant val = composition.getConstant(key);
                if (val == null) {
                    return Suggestions.empty();
                }

                valueType = val.getType();
            }

            if (type == SuggestionType.TIMEVARIABLE_VALUE) {
                TimeVariable attr = composition.getTimeVariable(key);
                if (attr == null) {
                    return Suggestions.empty();
                }

                valueType = attr.getType();
            }

            switch (valueType) {
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
