package dev.polv.polcinematics.commands.suggetions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.polv.polcinematics.cinematic.compositions.ICompositionType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class CompositionTypeSuggestion implements SuggestionProvider<ServerCommandSource> {

    private final ICompositionType[] types;

    /*private static <T extends Enum<T> & ICompositionType> List<ICompositionType> getAllEntries(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(ICompositionType.class::cast)
                .collect(Collectors.toList());
    }

    public CompositionTypeSuggestion(Class<? extends Enum> enumClass) {
        this(getAllEntries(enumClass).toArray(new ICompositionType[0]));
    }*/

    public CompositionTypeSuggestion(ICompositionType[] types) {
        this.types = types;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (ICompositionType type : types) {
            builder.suggest(type.getName());
        }
        return builder.buildFuture();
    }
}
