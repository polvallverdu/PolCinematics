package engineer.pol.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import engineer.pol.PolCinematics;
import engineer.pol.utils.math.Easing;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EasingSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String easing = context.getArgument("easing", String.class);
            Easing.getValues().keySet().stream().filter(easingName -> easingName.toLowerCase().contains(easing.toLowerCase())).forEach(builder::suggest);
        } catch (Exception ignore) {}
        return builder.buildFuture();
    }

}
