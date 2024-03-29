package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.utils.math.Easing;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class EasingSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String easing = ctx.getArgument("easing", String.class);
            Easing.getValues().keySet().stream().filter(easingName -> easingName.toLowerCase().contains(easing.toLowerCase())).forEach(builder::suggest);
        } catch (Exception ignore) {
            Easing.getValues().keySet().forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

}
