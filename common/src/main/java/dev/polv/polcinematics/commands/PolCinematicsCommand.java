package dev.polv.polcinematics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.commands.subcommands.*;
import dev.polv.polcinematics.utils.BridagierUtils;
import dev.polv.polcinematics.utils.ChatUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Function;

public class PolCinematicsCommand {

    public final static String PREFIX = "§8[§3PolCinematics§8]§r ";
    private static final String HELP_MESSAGE = ChatUtils.formatHelpMessage(
            "cp", "Player for cinematics",
            "cg", "Group players to manage them",
            "ce", "Cinematic editor",
            "cm", "Cinematic manager",
            "cpm", "Cinematic media player"
    );

    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(Text.of("Invalid UUID"));
    public static final SimpleCommandExceptionType CINEMATIC_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Cinematic not found"));
    public static final SimpleCommandExceptionType CINEMATIC_NOT_SELECTED = new SimpleCommandExceptionType(Text.of("Cinematic not selected. Select with /cm select <name>"));
    public static final SimpleCommandExceptionType INVALID_TIMELINE = new SimpleCommandExceptionType(Text.of("Timeline not found"));
    public static final SimpleCommandExceptionType INVALID_COMPOSITION = new SimpleCommandExceptionType(Text.of("Compsositing not found"));
    public static final SimpleCommandExceptionType INVALID_PROPERTY = new SimpleCommandExceptionType(Text.of("Invalid property key"));
    public static final SimpleCommandExceptionType INVALID_ATTRIBUTE = new SimpleCommandExceptionType(Text.of("Invalid attribute key"));
    public static final SimpleCommandExceptionType INVALID_KEYFRAME = new SimpleCommandExceptionType(Text.of("There's no keyframe at this time"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> mainBuilder = CommandManager.literal("polcinematics");
        mainBuilder.then(CommandManager.literal("help").executes(ctx -> {
            ctx.getSource().sendMessage(Text.literal(HELP_MESSAGE));
            return 1;
        }));
        mainBuilder.executes(PolCinematicsCommand::version);
        mainBuilder.then(
                CommandManager
                        .literal("version")
                        .executes(PolCinematicsCommand::version)
        );

        // Building nodes
        LiteralCommandNode<ServerCommandSource> playerNode = PlayerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> groupNode = GroupSubcommand.build();
        LiteralCommandNode<ServerCommandSource> mediaPlayerNode = MediaPlayerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> managerNode = ManagerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> editorNode = EditorSubcommand.build();

        // Registering subcommands to /polcinematics
        mainBuilder.then(playerNode); // /polcinematics player
        mainBuilder.then(groupNode); // /polcinematics groups
        mainBuilder.then(mediaPlayerNode); // /polcinematics mediaplayer
        mainBuilder.then(managerNode); // /polcinematics manager
        mainBuilder.then(editorNode); // /polcinematics editor

        // Creating main command /polcinematics
        // LiteralCommandNode<ServerCommandSource> mainNode = mainBuilder.build();

        // Creating aliases
        LiteralArgumentBuilder<ServerCommandSource>[] aliases = new LiteralArgumentBuilder[]{
                BridagierUtils.goodRedirect("cp", playerNode),
                BridagierUtils.goodRedirect("cg", groupNode),
                BridagierUtils.goodRedirect("cmp", mediaPlayerNode),
                BridagierUtils.goodRedirect("cm", managerNode),
                BridagierUtils.goodRedirect("ce", editorNode),
        };

        // Registering main command and aliases
        dispatcher.register(mainBuilder);
        for (LiteralArgumentBuilder alias : aliases) {
            dispatcher.register(alias);
        }
    }

    private static int version(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.of(
                """
                §8=========================================
                
                §aThis server is using §3PolCinematics
                §aMade by: §6Pol Vallverdu (polv.dev)
                §aVersion: §3%version%
                §aWebsite: §3cinematics.polv.dev
                
                §8=========================================
                """.replaceAll("%version%", "ALPHA") // TODO: Set version
        ));

        return 1;
    }

}
