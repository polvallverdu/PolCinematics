package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class CinematicNameSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String cinematicname = context.getArgument("cinematicname", String.class);
            PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().stream().filter(cinematic -> cinematic.getName().toLowerCase().contains(cinematicname.toLowerCase())).forEach(cinematic -> builder.suggest(cinematic.getName()));
        } catch (Exception ignore) {}
        return builder.buildFuture();
    }

}
