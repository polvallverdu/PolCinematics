package dev.polv.polcinematics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.camera.SlerpCameraComposition;
import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.suggetions.CinematicFileSuggetion;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class CinematicCommand {

    private static HashMap<UUID, UUID> selectedCinematics = new HashMap<>();

    public final static String PREFIX = "§8[§3PolCinematics§8]§r ";
    private static String helpCommand = PREFIX + "§6List of commands: \n\n" +
            "§6/polcinematics help §8- §Shows this message\n" +
            "§6/polcinematics list §8- §Shows a list of all loaded cinematics\n" +
            "";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal("cinematic");

        literalBuilder.executes(CinematicCommand::help);
        literalBuilder.then(CommandManager.literal("help").executes(CinematicCommand::help));

        literalBuilder.then(CommandManager.literal("select").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicLoadedSuggestion()).executes(CinematicCommand::select)));
        literalBuilder.then(CommandManager.literal("load").then(CommandManager.argument("filename", StringArgumentType.string()).suggests(new CinematicFileSuggetion()).executes(CinematicCommand::load)));
        literalBuilder.then(CommandManager.literal("unload").then(CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicLoadedSuggestion()).executes(CinematicCommand::unload)));
        literalBuilder.then(CommandManager.literal("create").then(CommandManager.argument("cinematicname", StringArgumentType.word()).executes(CinematicCommand::create)));
        literalBuilder.then(CommandManager.literal("save").executes(CinematicCommand::save));

        literalBuilder.then(CommandManager.literal("list").executes(CinematicCommand::list));
        literalBuilder.then(CommandManager.literal("listfiles").executes(CinematicCommand::listfiles));

        literalBuilder.then(CameraSubcommand.register(CommandManager.literal("camera"), registryAccess, environment));
        literalBuilder.then(ControlSubcommand.register(CommandManager.literal("control"), registryAccess, environment));

        literalBuilder.then(CommandManager.literal("test1").executes(CinematicCommand::test1));
        literalBuilder.then(CommandManager.literal("test2").executes(CinematicCommand::test2));
        literalBuilder.then(CommandManager.literal("test3").executes(CinematicCommand::test3));
        literalBuilder.then(CommandManager.literal("test4").executes(CinematicCommand::test4));


        dispatcher.register(literalBuilder);
    }

    private static int help(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.of(helpCommand));
        return 1;
    }

    private static int select(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("cinematicname", String.class);
        UUID uuid = context.getSource().getPlayer().getUuid();
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);
        if (cinematic != null) {
            selectedCinematics.put(uuid, cinematic.getUuid());
            context.getSource().sendMessage(Text.of(PREFIX + "§aSelected cinematic §6" + name));
        } else {
            context.getSource().sendMessage(Text.of(PREFIX + "§cCinematic §6" + name + " §cnot found"));
        }
        return 1;
    }

    private static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.createCinematic(context.getArgument("cinematicname", String.class), 10000);
        } catch (NameException e) {
            context.getSource().sendMessage(Text.of(PREFIX + "§cCinematic name §6" + context.getArgument("cinematicname", String.class) + " §cis already taken"));
            return 1;
        }

        context.getSource().sendMessage(Text.of(PREFIX + "§aCreated cinematic §6" + cinematic.getName()));

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PREFIX + "§aSelected cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int load(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("filename", String.class);

        ServerCinematicManager.SimpleCinematic filename = PolCinematics.CINEMATICS_MANAGER.getSimpleCinematic(name);

        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.loadCinematic(filename.getUuid() + ".json");
        } catch (InvalidCinematicException e) {
            context.getSource().sendMessage(Text.of(PREFIX + "§cCinematic §6" + name + " §cnot found"));
            e.printStackTrace();
            return 1;
        }

        context.getSource().sendMessage(Text.of(PREFIX + "§aLoaded cinematic §6" + cinematic.getName()));

        selectedCinematics.put(context.getSource().getPlayer().getUuid(), cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PREFIX + "§aSelected cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int unload(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("cinematicname", String.class);
        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);

        if (cinematic == null) {
            context.getSource().sendMessage(Text.of(PREFIX + "§cCinematic §6" + name + " §cnot found"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());
        PolCinematics.CINEMATICS_MANAGER.unloadCinematic(cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PREFIX + "§aSaved and unloaded cinematic §6" + cinematic.getName()));

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
            context.getSource().sendMessage(Text.of(PREFIX + "§cYou don't have any cinematic selected"));
            return 1;
        }

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic.getUuid());

        context.getSource().sendMessage(Text.of(PREFIX + "§aSaved cinematic §6" + cinematic.getName()));
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.of(PREFIX + "§aCinematics: " + PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().stream().map(Cinematic::getName).collect(Collectors.joining(", "))));
        return 1;
    }

    private static int listfiles(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.of(PREFIX + "§aCinematics: §f" + Stream.of(PolCinematics.CINEMATICS_MANAGER.getCinematicFiles()).map(f -> PolCinematics.CINEMATICS_MANAGER.isCinematicLoaded(f) ? f + " (§aLOADED§f)" : f + " (§cUNLOADED§f)").collect(Collectors.joining(", "))));
        return 1;
    }

    protected static Cinematic getCinematic(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (selectedCinematics.containsKey(uuid)) {
            return PolCinematics.CINEMATICS_MANAGER.getCinematic(selectedCinematics.get(uuid));
        }
        return null;
    }

    private static int test1(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic c = getCinematic(context.getSource().getPlayer());
        var todelete = c.getCameraTimeline().getWrappedComposition(0l);
        c.getCameraTimeline().replaceComposition(todelete.getUUID(), new SlerpCameraComposition("slerp", todelete.getDuration()));
        return 1;
    }

    private static int test2(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic c = getCinematic(context.getSource().getPlayer());
        Vec3d playerLoc = context.getSource().getPlayer().getPos();
        float playerYaw = context.getSource().getPlayer().getYaw();
        float playerPitch = context.getSource().getPlayer().getPitch();

        c.getCameraComposition(0l).getAttribute("position").setKeyframe(0l, new CameraPos(playerLoc.x, playerLoc.y, playerLoc.z, playerPitch, playerYaw, 0, 90));

        return 1;
    }

    private static int test3(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic c = getCinematic(context.getSource().getPlayer());
        Vec3d playerLoc = context.getSource().getPlayer().getPos();
        float playerYaw = context.getSource().getPlayer().getYaw();
        float playerPitch = context.getSource().getPlayer().getPitch();

        c.getCameraComposition(0l).getAttribute("position").setKeyframe(2500l, new CameraPos(playerLoc.x, playerLoc.y, playerLoc.z, playerPitch, playerYaw, 0, 90));

        return 1;
    }

    private static int test4(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic c = getCinematic(context.getSource().getPlayer());
        Vec3d playerLoc = context.getSource().getPlayer().getPos();
        float playerYaw = context.getSource().getPlayer().getYaw();
        float playerPitch = context.getSource().getPlayer().getPitch();

        c.getCameraComposition(0l).getAttribute("position").setKeyframe(5000l, new CameraPos(playerLoc.x, playerLoc.y, playerLoc.z, playerPitch, playerYaw, 0, 90));

        return 1;
    }
}
