package dev.polv.polcinematics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.commands.subcommands.EditorSubcommand;
import dev.polv.polcinematics.commands.subcommands.MediaPlayerSubcommand;
import dev.polv.polcinematics.commands.subcommands.PlayerSubcommand;
import dev.polv.polcinematics.commands.subcommands.ManagerSubcommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PolCinematicsCommand {

    public final static String PREFIX = "§8[§3PolCinematics§8]§r ";
    private static final String HELP_MESSAGE = PREFIX + "§bList of subcommands: \n\n"
            + "§3/cp §8- §bPlayer for cinematics\n"
            + "§3/cpm §8- §bCinematic media player\n"
            + "§3/cm §8- §bCinematic manager\n"
            ;

    public static final SimpleCommandExceptionType CINEMATIC_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Cinematic not found"));
    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(Text.of("Invalid UUID"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> mainBuilder = CommandManager.literal("polcinematics");
        mainBuilder.then(CommandManager.literal("help").executes(ctx -> {
            ctx.getSource().sendMessage(Text.literal(HELP_MESSAGE));
            return 1;
        }));

        // Building nodes
        LiteralCommandNode<ServerCommandSource> playerNode = PlayerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> mediaPlayerNode = MediaPlayerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> managerNode = ManagerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> editorNode = EditorSubcommand.build();

        // Registering subcommands to /polcinematics
        mainBuilder.then(playerNode); // /polcinematics player
        mainBuilder.then(mediaPlayerNode); // /polcinematics mediaplayer
        mainBuilder.then(managerNode); // /polcinematics manager
        mainBuilder.then(editorNode); // /polcinematics editor

        // Creating main command /polcinematics
        // LiteralCommandNode<ServerCommandSource> mainNode = mainBuilder.build();

        // Creating aliases
        LiteralArgumentBuilder<ServerCommandSource>[] aliases = new LiteralArgumentBuilder[]{
            CommandManager.literal("cp").redirect(playerNode),
            CommandManager.literal("cmp").redirect(mediaPlayerNode),
            CommandManager.literal("cm").redirect(managerNode),
            CommandManager.literal("ce").redirect(editorNode)
        };

        // Registering main command and aliases
        dispatcher.register(mainBuilder);
        for (LiteralArgumentBuilder alias : aliases) {
            dispatcher.register(alias);
        }
    }

}
