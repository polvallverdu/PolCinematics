package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.commands.subcommands.ManagerSubcommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CinematicThingsSuggestion implements SuggestionProvider<ServerCommandSource> {

    public enum SuggestionType {
        TIMELINE,
        COMPOSITION,
        PROPERTY_KEYS,
        PROPERTY_VALUE,
        ATTRIBUTE_KEYS,
        ATTRIBUTE_VALUE,
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

        Cinematic cinematic = ManagerSubcommand.getSelectedCinematic(player);

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
            getCompositionNames(timeline).forEach(builder::suggest);
            getCompositionUuids(timeline).forEach(builder::suggest);

            return builder.buildFuture();
        }

        String compositionname = StringArgumentType.getString(context, "composition");
        Timeline.WrappedComposition wrappedComposition = timeline.findWrappedComposition(compositionname);

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

        return builder.buildFuture();
    }

    private static List<String> getCompositionNames(Timeline timeline) {
        return timeline.getWrappedCompositions().stream().map(wc -> wc.getComposition().getName()).collect(Collectors.toList());
    }

    private static List<String> getCompositionUuids(Timeline timeline) {
        return timeline.getWrappedCompositions().stream().map(wc -> wc.getComposition().getUuid().toString()).collect(Collectors.toList());
    }
}
