package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class CinematicTimelineSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String cinematicname = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicname);
        if (cinematic != null) {
            builder.suggest("camera");
            for (int i = 0; i < cinematic.getTimelineCount(); i++) {
                builder.suggest(String.valueOf(i));
            }
        }

        return builder.buildFuture();
    }

}
