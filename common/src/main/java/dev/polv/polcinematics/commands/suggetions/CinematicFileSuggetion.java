package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class CinematicFileSuggetion implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String tfilename = "";
        try {
            tfilename = context.getArgument("filename", String.class);
        } catch (Exception ignore) {}

        final String filename = tfilename;

        PolCinematics.CINEMATICS_MANAGER.getSimpleCinematics().stream().filter(cinematic -> cinematic.name().toLowerCase().startsWith(filename.toLowerCase())).forEach(cinematic -> builder.suggest(cinematic.name()));
        PolCinematics.CINEMATICS_MANAGER.getSimpleCinematics().stream().filter(cinematic -> cinematic.uuid().toString().toLowerCase().startsWith(filename.toLowerCase())).forEach(cinematic -> builder.suggest(cinematic.uuid().toString()));

        return builder.buildFuture();
    }
}
