package engineer.pol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import engineer.pol.PolCinematics;
import engineer.pol.cinematic.Cinematic;
import engineer.pol.commands.suggetions.CinematicFileSuggetion;
import engineer.pol.commands.suggetions.CinematicNameSuggestion;
import engineer.pol.exception.InvalidCinematicException;
import engineer.pol.exception.NameException;
import engineer.pol.net.Packets;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class CinematicCommand {

    private static HashMap<UUID, UUID> selectedCinematics = new HashMap<>();

    private static String prefix = "§8[§3PolCinematics§8]§r ";
    private static String helpCommand = prefix + "§6List of commands: \n\n" +
            "§6/polcinematics help §8- §Shows this message\n" +
            "§6/polcinematics list §8- §Shows a list of all loaded cinematics\n" +
            "";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal("cinematic");

        literalBuilder.executes(CinematicCommand::help);
        literalBuilder.then(CommandManager.literal("help").executes(CinematicCommand::help));

        literalBuilder.then(CommandManager.literal("select").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicNameSuggestion()).executes(CinematicCommand::select)));
        literalBuilder.then(CommandManager.literal("load").then(CommandManager.argument("filename", StringArgumentType.string()).suggests(new CinematicFileSuggetion()).executes(CinematicCommand::load)));
        literalBuilder.then(CommandManager.literal("unload").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicNameSuggestion()).executes(CinematicCommand::unload)));
        literalBuilder.then(CommandManager.literal("create").then(CommandManager.argument("cinematicname", StringArgumentType.word()).executes(CinematicCommand::create)));
        literalBuilder.then(CommandManager.literal("save").executes(CinematicCommand::save));

        literalBuilder.then(CommandManager.literal("broadcast").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicNameSuggestion()).executes(CinematicCommand::broadcast)));
        literalBuilder.then(CommandManager.literal("play").executes(CinematicCommand::play));
        literalBuilder.then(CommandManager.literal("stop").executes(CinematicCommand::stop));

        literalBuilder.then(CommandManager.literal("list").executes(CinematicCommand::list));
        literalBuilder.then(CommandManager.literal("listfiles").executes(CinematicCommand::listfiles));

        literalBuilder.then(CameraSubcommand.register(CommandManager.literal("camera"), registryAccess, environment));
        literalBuilder.then(EditorSubcommand.register(CommandManager.literal("editor"), registryAccess, environment));

        dispatcher.register(literalBuilder);
    }

    private static int help(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of(helpCommand), false);
        return 1;
    }

    private static int select(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("cinematicname", String.class);
        UUID uuid = context.getSource().getPlayer().getUuid();
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);
        if (cinematic != null) {
            selectedCinematics.put(uuid, cinematic.getUuid());
            context.getSource().sendFeedback(Text.of(prefix + "§aSelected cinematic §6" + name), false);
        } else {
            context.getSource().sendFeedback(Text.of(prefix + "§cCinematic §6" + name + " §cnot found"), false);
        }
        return 1;
    }

    private static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.createCinematic(context.getArgument("cinematicname", String.class), 10000);
        } catch (NameException e) {
            context.getSource().sendFeedback(Text.of(prefix + "§cCinematic name §6" + context.getArgument("cinematicname", String.class) + " §cis already taken"), false);
            return 1;
        }

        context.getSource().sendFeedback(Text.of(prefix + "§aCreated cinematic §6" + cinematic.getName()), false);

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendFeedback(Text.of(prefix + "§aSelected cinematic §6" + cinematic.getName()), false);
        return 1;
    }

    private static int load(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("filename", String.class);

        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.loadCinematic(name);
        } catch (InvalidCinematicException e) {
            context.getSource().sendFeedback(Text.of(prefix + "§cCinematic §6" + name + " §cnot found"), false);
            return 1;
        }

        context.getSource().sendFeedback(Text.of(prefix + "§aLoaded cinematic §6" + cinematic.getName()), false);

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendFeedback(Text.of(prefix + "§aSelected cinematic §6" + cinematic.getName()), false);
        return 1;
    }

    private static int unload(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);

        if (cinematic == null) {
            context.getSource().sendFeedback(Text.of(prefix + "§cCinematic §6" + name + " §cnot found"), false);
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());
        PolCinematics.CINEMATICS_MANAGER.unloadCinematic(cinematic.getUuid());

        context.getSource().sendFeedback(Text.of(prefix + "§aSaved and unloaded cinematic §6" + cinematic.getName()), false);

        new HashMap<>(selectedCinematics).forEach((uuid, cinematicUuid) -> {
            if (cinematicUuid.equals(cinematic.getUuid())) {
                selectedCinematics.remove(uuid);
            }
        });
        return 1;
    }

    private static int save(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(context.getSource().getPlayer());
        if (cinematic == null) {
            context.getSource().sendFeedback(Text.of(prefix + "§cYou don't have any cinematic selected"), false);
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());

        context.getSource().sendFeedback(Text.of(prefix + "§aSaved cinematic §6" + cinematic.getName()), false);
        return 1;
    }

    private static int broadcast(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String cinematicname = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicname);

        if (cinematic == null) {
            context.getSource().sendFeedback(Text.of(prefix + "§cCinematic §6" + cinematicname + " §cnot found"), false);
            return 1;
        }

        Packets.broadcastCinematic(cinematic, context.getSource().getServer().getPlayerManager().getPlayerList());

        return 1;
    }

    private static int play(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicPlay(context.getSource().getServer().getPlayerManager().getPlayerList());

        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Packets.sendCinematicStop(context.getSource().getServer().getPlayerManager().getPlayerList());

        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of(prefix + "§aCinematics: " + PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().stream().map(Cinematic::getName).collect(Collectors.joining(", "))), false);
        return 1;
    }

    private static int listfiles(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of(prefix + "§aCinematics: §f" + Stream.of(PolCinematics.CINEMATICS_MANAGER.getCinematicFiles()).map(f -> PolCinematics.CINEMATICS_MANAGER.isCinematicLoaded(f) ? f + " (§aLOADED§f)" : f + " (§cUNLOADED§f)").collect(Collectors.joining(", "))), false);
        return 1;
    }

    protected static Cinematic getCinematic(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (selectedCinematics.containsKey(uuid)) {
            return PolCinematics.CINEMATICS_MANAGER.getCinematic(selectedCinematics.get(uuid));
        }
        return null;
    }
}
