package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.manager.FileCinematic;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.exception.AlreadyLoadedCinematicException;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.utils.CommandUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

final public class ManagerSubcommand {

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> managerArgumentBuilder = CommandManager.literal("manager");

        managerArgumentBuilder.then(
                CommandManager.literal("select")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(ManagerSubcommand::select)
                        )
        );

        managerArgumentBuilder.then(
                CommandManager.literal("load")
                        .then(
                                CommandUtils.arg_filecinematic()
                                        .executes(ManagerSubcommand::load)
                        )
        );

        managerArgumentBuilder.then(
                CommandManager.literal("unload")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(ManagerSubcommand::unload)
                        )
        );

        managerArgumentBuilder.then(
                CommandManager.literal("create")
                        .then(
                                CommandManager.argument("cinematic", StringArgumentType.word())
                                        .executes(ManagerSubcommand::create)
                        )
        );

        managerArgumentBuilder.then(
                CommandManager.literal("save")
                        .then(
                                CommandUtils.arg_cinematic()
                                        .executes(ManagerSubcommand::save)
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

    private static int select(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
        }

        PolCinematics.CINEMATICS_MANAGER.selectCinematic(player, cinematic);
        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSelected cinematic §6" + cinematic.getName()));

        return 1;
    }

    private static int create(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic;
        try {
            cinematic = PolCinematics.CINEMATICS_MANAGER.createCinematic(ctx.getArgument("cinematic", String.class), 10000);
        } catch (NameException e) {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cCinematic name §6" + ctx.getArgument("cinematic", String.class) + " §cis already taken"));
            return 1;
        }

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aCreated cinematic §6" + cinematic.getName()));

        if (ctx.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
            PolCinematics.CINEMATICS_MANAGER.selectCinematic(player, cinematic);

            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSelected cinematic §6" + cinematic.getName()));
        }
        return 1;
    }

    private static int load(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        FileCinematic fileCinematic = CommandUtils.getFileCinematic(ctx);

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§fLoading cinematic..."));

        PolCinematics.CINEMATICS_MANAGER.loadCinematic(fileCinematic.uuid() + ".json", (e) -> {
            try {
                throw e;
            } catch (InvalidCinematicException ee) {
                ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + ee.getMessage()));
                e.printStackTrace();
            } catch (AlreadyLoadedCinematicException ee) {
                ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + ee.getMessage()));
            } catch (Exception ee) {
                ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cSomething went wrong while loading cinematic. Check console for more info"));
                ee.printStackTrace();
            }
        }, (cinematic) -> {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Loaded cinematic §f" + cinematic.getName()));

            if (ctx.getSource().isExecutedByPlayer()) {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                PolCinematics.CINEMATICS_MANAGER.selectCinematic(player, cinematic);

                ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Selected cinematic §f" + cinematic.getName()));
            }
        });
        return 1;
    }

    private static int unload(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx, false);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_FOUND.create();
        }
        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Saving cinematic..."));

        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic, () -> {
            PolCinematics.CINEMATICS_MANAGER.unloadCinematic(cinematic);

            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSaved and unloaded cinematic §f" + cinematic.getName()));
        });
        return 1;
    }

    private static int save(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Cinematic cinematic = CommandUtils.getCinematic(ctx);

        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Saving cinematic..."));
        PolCinematics.CINEMATICS_MANAGER.saveCinematic(cinematic, () -> {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aSaved cinematic §6" + cinematic.getName()));
        });
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> ctx) {
        MutableText msg = Text.literal(PolCinematicsCommand.PREFIX + "§7Cinematics: §f");

        PolCinematics.CINEMATICS_MANAGER.getLoadedCinematics().forEach(c -> {
            MutableText ctext = Text.literal(c.getName() + " ");

            ctext.append(Text.literal("[UNLOAD] ").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm unload " + c.getName()))));
            ctext.append(Text.literal("[SELECT] ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm select " + c.getName()))));
            ctext.append(Text.literal("[SAVE]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cm save " + c.getName()))));

            msg.append("\n").append(ctext);
        });

        ctx.getSource().sendMessage(msg);
        return 1;
    }

    private static int listfiles(CommandContext<ServerCommandSource> ctx) {
        MutableText msg = Text.literal(PolCinematicsCommand.PREFIX + "§7File Cinematics: §f");

        PolCinematics.CINEMATICS_MANAGER.getFileCinematics().forEach(sc -> {
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

        ctx.getSource().sendMessage(msg);
        return 1;
    }


}
