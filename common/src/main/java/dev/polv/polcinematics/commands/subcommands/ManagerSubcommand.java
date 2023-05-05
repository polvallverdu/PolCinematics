package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.manager.SimpleCinematic;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.suggetions.CinematicFileSuggetion;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.exception.AlreadyLoadedCinematicException;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.UUID;

final public class ManagerSubcommand {

    private static HashMap<UUID, UUID> selectedCinematics = new HashMap<>();

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> managerArgumentBuilder = CommandManager.literal("manager");

        managerArgumentBuilder.then(CommandManager.literal("select").then(CommandManager.argument("cinematicname", StringArgumentType.word()).suggests(new CinematicLoadedSuggestion()).executes(ManagerSubcommand::select)));
        managerArgumentBuilder.then(CommandManager.literal("load").then(CommandManager.argument("filename", StringArgumentType.string()).suggests(new CinematicFileSuggetion()).executes(ManagerSubcommand::load)));
        managerArgumentBuilder.then(CommandManager.literal("unload").then(CommandManager.argument("cinematicname", StringArgumentType.word()).suggests(new CinematicLoadedSuggestion()).executes(ManagerSubcommand::unload)));
        managerArgumentBuilder.then(CommandManager.literal("create").then(CommandManager.argument("cinematicname", StringArgumentType.word()).executes(ManagerSubcommand::create)));
        managerArgumentBuilder.then(
                CommandManager.literal("save")
                        .then(
                                CommandManager.argument("cinematicname", StringArgumentType.word()).suggests(new CinematicLoadedSuggestion()).executes(ManagerSubcommand::save)
                        )
                        .executes(ManagerSubcommand::save)
        );
        managerArgumentBuilder.then(
                CommandManager.literal("list")
                        .then(CommandManager.literal("loaded").executes(ManagerSubcommand::list))
                        .then(CommandManager.literal("files").executes(ManagerSubcommand::listfiles))
                        .executes(ManagerSubcommand::listfiles)
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

    private static int create(CommandContext<ServerCommandSource> context) {
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

    private static int load(CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("filename", String.class);

        SimpleCinematic filename = PolCinematics.CINEMATICS_MANAGER.getSimpleCinematic(name);

        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.loadCinematic(filename.uuid() + ".json");
        } catch (InvalidCinematicException e) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + name + " §cnot found"));
            e.printStackTrace();
            return 1;
        } catch (AlreadyLoadedCinematicException e) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic §6" + name + " §cis already loaded"));
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

    private static int save(CommandContext<ServerCommandSource> context) {
        Cinematic cinematic;

        try {
            String cinematicName = StringArgumentType.getString(context, "cinematicname");
            cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicName);
        } catch (Exception e) {
            cinematic = getSelectedCinematic(context.getSource().getPlayer());
        }

        if (cinematic == null) {
            context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cYou don't have any cinematic selected"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSaved cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        MutableText msg = Text.literal(PolCinematicsCommand.PREFIX + "§7Cinematics: §f");

        PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().forEach(c -> {
            MutableText ctext = Text.literal(c.getName() + " ");

            ctext.append(Text.literal("[UNLOAD] ").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm unload " + c.getName()))));
            ctext.append(Text.literal("[SELECT] ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm select " + c.getName()))));
            ctext.append(Text.literal("[SAVE]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm save " + c.getName()))));

            msg.append("\n").append(ctext);
        });

        context.getSource().sendMessage(msg);
        return 1;
    }

    private static int listfiles(CommandContext<ServerCommandSource> context) {
        MutableText msg = Text.literal(PolCinematicsCommand.PREFIX + "§7File Cinematics: §f");

        PolCinematics.CINEMATICS_MANAGER.getSimpleCinematics().forEach(sc -> {
            boolean isLoaded = PolCinematics.CINEMATICS_MANAGER.isCinematicLoaded(sc.uuid());

            MutableText ctext = Text.literal("§f[" + (isLoaded ? "§aLOADED" : "§cUNLOADED") + "§f] " + sc.name() + " ");

            if (isLoaded) {
                ctext.append(Text.literal("[UNLOAD] ").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm unload " + sc.name()))));
                ctext.append(Text.literal("[SELECT] ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm select " + sc.name()))));
                ctext.append(Text.literal("[SAVE]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm save " + sc.name()))));
            } else {
                ctext.append(Text.literal("[LOAD]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm load " + sc.uuid()))));
            }

            msg.append("\n").append(ctext);
        });

        context.getSource().sendMessage(msg);
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
