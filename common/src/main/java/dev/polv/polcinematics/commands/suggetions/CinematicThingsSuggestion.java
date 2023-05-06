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
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.compositions.attributes.Attribute;
import dev.polv.polcinematics.cinematic.compositions.value.EValueType;
import dev.polv.polcinematics.cinematic.compositions.value.Value;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.commands.subcommands.ManagerSubcommand;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class CinematicThingsSuggestion implements SuggestionProvider<ServerCommandSource> {

    public enum SuggestionType {
        TIMELINE,
        COMPOSITION,
        PROPERTY_KEYS,
        PROPERTY_VALUE,
        ATTRIBUTE_KEYS,
        ATTRIBUTE_VALUE,
        ATTRIBUTE_POSITION,
    }

    private final SuggestionType type;

    public CinematicThingsSuggestion(SuggestionType type) {
        this.type = type;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (player == null) {
            return Suggestions.empty();
        }

        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getSelectedCinematic(player);

        if (cinematic == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.TIMELINE) {
            builder.suggest("camera");
            for (int i = 0; i < cinematic.getTimelineCount(); i++) {
                builder.suggest(String.valueOf(i+1));
            }

            return builder.buildFuture();
        }

        String timelinename = StringArgumentType.getString(context, "timeline");
        Timeline timeline = cinematic.resolveTimeline(timelinename);

        if (timeline == null) {
            return Suggestions.empty();
        }

        if (type == SuggestionType.COMPOSITION) {
            timeline.getWrappedCompositions().forEach(wc -> {
                builder.suggest(wc.getUUID().toString(), Text.of(wc.getComposition().getName()));
                builder.suggest(wc.getComposition().getName());
            });

            return builder.buildFuture();
        }

        String compositionname = StringArgumentType.getString(context, "composition");
        WrappedComposition wrappedComposition = timeline.findWrappedComposition(compositionname);

        if (wrappedComposition == null) {
            return Suggestions.empty();
        }

        Composition composition = wrappedComposition.getComposition();

        if (type == SuggestionType.ATTRIBUTE_KEYS) {
            composition.getAttributesList().getKeys().forEach(builder::suggest);
            return builder.buildFuture();
        }

        if (type == SuggestionType.PROPERTY_KEYS) {
            composition.getProperties().getKeys().forEach(builder::suggest);
            return builder.buildFuture();
        }

        if (type == SuggestionType.ATTRIBUTE_POSITION) {
            composition.getAttributesList().getAttributes().forEach(attribute -> {
                attribute.getAllKeyframes().forEach(kf -> builder.suggest(String.valueOf(kf.getTime())));
            });
            return builder.buildFuture();
        }

        if (type == SuggestionType.PROPERTY_VALUE || type == SuggestionType.ATTRIBUTE_VALUE) {
            EValueType valueType = null;

            if (type == SuggestionType.PROPERTY_VALUE) {
                String propertyValue = StringArgumentType.getString(context, "property");
                Value val = composition.getProperty(propertyValue);
                if (val == null) {
                    return Suggestions.empty();
                }

                valueType = val.getType();
            }

            if (type == SuggestionType.ATTRIBUTE_VALUE) {
                String attributeKey = StringArgumentType.getString(context, "attribute");
                Attribute attr = composition.getAttribute(attributeKey);
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
                    return Vec3ArgumentType.vec3().listSuggestions(context, builder);
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
