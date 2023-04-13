package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.suggetions.CinematicFileSuggetion;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class ManagerSubcommand {

    private static HashMap<UUID, UUID> selectedCinematics = new HashMap<>();

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> managerArgumentBuilder = CommandManager.literal("manager");

        managerArgumentBuilder.then(CommandManager.literal("select").then(CommandManager.argument("cinematicname", StringArgumentType.word()).suggests(new CinematicLoadedSuggestion()).executes(ManagerSubcommand::select)));
        managerArgumentBuilder.then(CommandManager.literal("load").then(CommandManager.argument("filename", StringArgumentType.string()).suggests(new CinematicFileSuggetion()).executes(ManagerSubcommand::load)));
        managerArgumentBuilder.then(CommandManager.literal("unload").then(CommandManager.argument("cinematicname", StringArgumentType.word()).suggests(new CinematicLoadedSuggestion()).executes(ManagerSubcommand::unload)));
        managerArgumentBuilder.then(CommandManager.literal("create").then(CommandManager.argument("cinematicname", StringArgumentType.word()).executes(ManagerSubcommand::create)));
        managerArgumentBuilder.then(CommandManager.literal("save").executes(ManagerSubcommand::save));
        managerArgumentBuilder.then(
                CommandManager.literal("list")
                        .then(CommandManager.literal("loaded").executes(ManagerSubcommand::list))
                        .then(CommandManager.literal("files").executes(ManagerSubcommand::listfiles))
        );

        return managerArgumentBuilder.build();
    }

    private static int select(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UUID uuid = context.getSource().getPlayer().getUuid();
        String cinameticName = StringArgumentType.getString(context, "cinematicname");
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinameticName);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
        }

        selectedCinematics.put(uuid, cinematic.getUuid());
        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSelected cinematic §6" + cinematic.getName()));

        return 1;
    }

    private static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.createCinematic(context.getArgument("cinematicname", String.class), 10000);
        } catch (NameException e) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic name §6" + context.getArgument("cinematicname", String.class) + " §cis already taken"));
            return 1;
        }

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aCreated cinematic §6" + cinematic.getName()));

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSelected cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int load(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("filename", String.class);

        ServerCinematicManager.SimpleCinematic filename = PolCinematics.CINEMATICS_MANAGER.getSimpleCinematic(name);

        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.loadCinematic(filename.getUuid() + ".json");
        } catch (InvalidCinematicException e) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + name + " §cnot found"));
            e.printStackTrace();
            return 1;
        }

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aLoaded cinematic §6" + cinematic.getName()));

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSelected cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int unload(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String cinameticName = StringArgumentType.getString(context, "cinematicname");
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinameticName);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
        }

        new HashMap<>(selectedCinematics).forEach((uuid, cinematicUuid) -> {
            if (cinematicUuid.equals(cinematic.getUuid())) {
                selectedCinematics.remove(uuid);
            }
        });

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());
        PolCinematics.CINEMATICS_MANAGER.unloadCinematic(cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSaved and unloaded cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int save(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getSelectedCinematic(context.getSource().getPlayer());
        if (cinematic == null) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cYou don't have any cinematic selected"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSaved cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aCinematics: " + PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().stream().map(Cinematic::getName).collect(Collectors.joining(", "))));
        return 1;
    }

    private static int listfiles(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aCinematics: §f" + Stream.of(PolCinematics.CINEMATICS_MANAGER.getCinematicFiles()).map(f -> PolCinematics.CINEMATICS_MANAGER.isCinematicLoaded(f) ? f + " (§aLOADED§f)" : f + " (§cUNLOADED§f)").collect(Collectors.joining(", "))));
        return 1;
    }

    public static Cinematic getSelectedCinematic(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (selectedCinematics.containsKey(uuid)) {
            return PolCinematics.CINEMATICS_MANAGER.getCinematic(selectedCinematics.get(uuid));
        }
        return null;
    }

}
