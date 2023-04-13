package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.commands.subcommands.ManagerSubcommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class CompositionUUIDSuggestion implements SuggestionProvider<ServerCommandSource> {

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

        String timelineI = context.getArgument("timeline", String.class);
        Timeline timeline = cinematic.getTimeline(timelineI);

        if (timeline == null) {
            return Suggestions.empty();
        }

        timeline.getWrappedCompositions().forEach(wrappedComposition -> {
            builder.suggest(wrappedComposition.getComposition().getUuid().toString());
        });

        return builder.buildFuture();
    }

}
