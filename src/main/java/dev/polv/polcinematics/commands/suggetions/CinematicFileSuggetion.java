package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.PolCinematics;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CinematicFileSuggetion implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String filename = context.getArgument("filename", String.class);
            Stream.of(PolCinematics.CINEMATICS_MANAGER.getCinematicFiles()).filter(file -> file.toLowerCase().contains(filename.toLowerCase())).forEach(builder::suggest);
        } catch (Exception ignore) {}
        return builder.buildFuture();
    }
}
