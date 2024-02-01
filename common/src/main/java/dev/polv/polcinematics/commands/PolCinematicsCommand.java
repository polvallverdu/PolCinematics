package dev.polv.polcinematics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.commands.subcommands.*;
import dev.polv.polcinematics.utils.BridagierUtils;
import dev.polv.polcinematics.utils.ChatUtils;
import dev.polv.polcinematics.utils.CommandUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PolCinematicsCommand {

    public final static String PREFIX = "§8[§3PolCinematics§8]§r ";
    private static final String HELP_MESSAGE = ChatUtils.formatHelpMessage(
            "cp", "Player for cinematics",
            "cg", "Group players to manage them",
            "ce", "Cinematic editor",
            "cm", "Cinematic manager",
            "cmp", "Cinematic media player"
    );

    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(Text.of("Invalid UUID"));
    public static final SimpleCommandExceptionType GROUP_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Invalid group name"));
    public static final SimpleCommandExceptionType CINEMATIC_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Cinematic not found"));
    public static final SimpleCommandExceptionType CINEMATIC_NOT_SELECTED = new SimpleCommandExceptionType(Text.of("Cinematic not selected. Select with /cm select <name>"));
    public static final SimpleCommandExceptionType INVALID_TIMELINE = new SimpleCommandExceptionType(Text.of("Timeline not found"));
    public static final SimpleCommandExceptionType INVALID_COMPOSITION = new SimpleCommandExceptionType(Text.of("Compsositing not found"));
    public static final SimpleCommandExceptionType INVALID_CONSTANT = new SimpleCommandExceptionType(Text.of("Invalid constant key"));
    public static final SimpleCommandExceptionType INVALID_TIMEVARIABLE = new SimpleCommandExceptionType(Text.of("Invalid time variable key"));
    public static final SimpleCommandExceptionType INVALID_KEYFRAME = new SimpleCommandExceptionType(Text.of("There's no keyframe at this time"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> mainBuilder = CommandManager.literal("polcinematics");
        mainBuilder.then(CommandUtils.l("help").executes(ctx -> {
            ctx.getSource().sendMessage(Text.literal(HELP_MESSAGE));
            return 1;
        }));
        mainBuilder.executes(PolCinematicsCommand::version);
        mainBuilder.then(
                CommandManager.literal("version")
                        .executes(PolCinematicsCommand::version)
        );

        // Building nodes
        LiteralCommandNode<ServerCommandSource> playerNode = PlayerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> groupNode = GroupSubcommand.build();
        LiteralCommandNode<ServerCommandSource> managerNode = ManagerSubcommand.build();
        LiteralCommandNode<ServerCommandSource> editorNode = EditorSubcommand.build();
        LiteralCommandNode<ServerCommandSource> screensNode = ScreenSubcommand.build();

        // Registering subcommands to /polcinematics
        mainBuilder.then(playerNode); // /polcinematics player
        mainBuilder.then(groupNode); // /polcinematics groups
        mainBuilder.then(managerNode); // /polcinematics manager
        mainBuilder.then(editorNode); // /polcinematics editor
        mainBuilder.then(screensNode); // /polcinematics screens

        // Creating main command /polcinematics
        // LiteralCommandNode<ServerCommandSource> mainNode = mainBuilder.build();

        // Creating aliases
        LiteralArgumentBuilder<ServerCommandSource>[] aliases = new LiteralArgumentBuilder[]{
                BridagierUtils.goodRedirect("cp", playerNode),
                BridagierUtils.goodRedirect("cg", groupNode),
                BridagierUtils.goodRedirect("cm", managerNode),
                BridagierUtils.goodRedirect("ce", editorNode),
                BridagierUtils.goodRedirect("cs", screensNode),
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
                §8§l-----------------------------------------
                
                §7This server is using §3PolCinematics
                §7Made by: §fPol Vallverdu (polv.dev)
                §7Version: §a%version%
                §7Website: §ccinematics.polv.dev
                
                §8§l-----------------------------------------""".replaceAll("%version%", "ALPHA") // TODO: Set version
        ));

        return 1;
    }

}
