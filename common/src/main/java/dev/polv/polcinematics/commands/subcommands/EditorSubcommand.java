package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.compositions.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.ICompositionType;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.cinematic.compositions.values.constants.Constant;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.CompositionTimeVariables;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.TimeVariable;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.helpers.CommandCooldownHash;
import dev.polv.polcinematics.commands.suggetions.CinematicThingsSuggestion;
import dev.polv.polcinematics.commands.suggetions.EasingSuggestion;
import dev.polv.polcinematics.exception.DeleteKeyframeException;
import dev.polv.polcinematics.exception.InvalidCommandValueException;
import dev.polv.polcinematics.exception.InvalidValueException;
import dev.polv.polcinematics.exception.OverlapException;
import dev.polv.polcinematics.utils.*;
import dev.polv.polcinematics.utils.math.Easing;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorSubcommand {

    private static final String SUBCOMMANDS = ChatUtils.formatHelpMessage(
            "/ce create", "Create a new timeline or composition",
            "/ce delete", "Delete a timeline or composition",
            "/ce info", "Get information about a timeline or composition",
            "/ce duration", "Get or set the duration of a timeline or composition",
            "/ce constants", "Get or set the value of a constant of a timeline or composition",
            "/ce timevar", "Get or set the value of a timed variable of a timeline or composition",
            "/ce help", "Display this help message"
    );

    private static final String BOTTOM_LINE = "§8§l=====================================";

    private static final CommandCooldownHash DELETE_COOLDOWN = new CommandCooldownHash(Duration.ofSeconds(15));

    private static LiteralArgumentBuilder<ServerCommandSource> l(String name) {
        return CommandUtils.l(name);
    }

    private static <T> RequiredArgumentBuilder<ServerCommandSource, T> arg(String name, ArgumentType<T> argumentType) {
        return CommandUtils.arg(name, argumentType);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> arg_value(LiteralArgumentBuilder<ServerCommandSource> l, Command<ServerCommandSource> executor, @Nullable SuggestionProvider<ServerCommandSource> suggestionProvider) {
        return l
                .then(
                        arg("stringValue", StringArgumentType.string())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("entityValue", EntityArgumentType.entity())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("entitiesValue", EntityArgumentType.entities())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("vec3Value", Vec3ArgumentType.vec3())
                                .suggests(suggestionProvider)
                                .then(
                                        arg("pitch", FloatArgumentType.floatArg())
                                                .then(
                                                        arg("yaw", FloatArgumentType.floatArg())
                                                                .then(
                                                                        arg("roll", FloatArgumentType.floatArg())
                                                                                .executes(executor)
                                                                )
                                                                .executes(executor)
                                                )
                                                .executes(executor)
                                )
                                .executes(executor)
                )
                .executes(executor);
    }

    private static <T> RequiredArgumentBuilder<ServerCommandSource, T> arg_value(RequiredArgumentBuilder<ServerCommandSource, T> arg, Command<ServerCommandSource> executor, @Nullable SuggestionProvider<ServerCommandSource> suggestionProvider) {
        return arg
                .then(
                        arg("stringValue", StringArgumentType.greedyString())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("entityValue", EntityArgumentType.entity())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("entitiesValue", EntityArgumentType.entities())
                                .suggests(suggestionProvider)
                                .executes(executor)
                ).then(
                        arg("vec3Value", Vec3ArgumentType.vec3())
                                .suggests(suggestionProvider)
                                .then(
                                        arg("pitch", FloatArgumentType.floatArg())
                                                .then(
                                                        arg("yaw", FloatArgumentType.floatArg())
                                                                .then(
                                                                        arg("roll", FloatArgumentType.floatArg())
                                                                                .executes(executor)
                                                                )
                                                                .executes(executor)
                                                )
                                                .executes(executor)
                                )
                                .executes(executor)
                )
                .executes(executor);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> arg_timeline_composition(Command<ServerCommandSource> executor) {
        return arg("timeline", StringArgumentType.word())
                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                .then(
                        arg("composition", StringArgumentType.word())
                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.COMPOSITION))
                                .executes(executor)
                );
    }

    @SafeVarargs
    private static RequiredArgumentBuilder<ServerCommandSource, String> arg_timeline_composition(ArgumentBuilder<ServerCommandSource, ?> ...builder) {
        var compo_arg = arg("composition", StringArgumentType.word())
                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.COMPOSITION));
        for (var b : builder) {
            compo_arg.then(b);
        }
        return arg("timeline", StringArgumentType.word())
                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                .then(
                        compo_arg
                );
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> arg_timeline_composition(ArgumentBuilder<ServerCommandSource, ?> builder, Command<ServerCommandSource> executor) {
        return arg("timeline", StringArgumentType.word())
                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                .then(
                        arg("composition", StringArgumentType.word())
                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.COMPOSITION))
                                .then(builder)
                                .executes(executor)
                );
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        var editorBuilder = l("editor");

        //editorBuilder.then(TimelineSubcommands.build());
        editorBuilder.then(
                l("create")
                        .then(
                                l("timeline").executes((ctx) -> {
                                    Cinematic cinematic = CommandUtils.getCinematic(ctx);

                                    Timeline timeline = cinematic.addTimeline();

                                    ctx.getSource().sendMessage(Text.literal(PolCinematicsCommand.PREFIX + "Timeline " + cinematic.getTimelineCount() + " created"));
                                    return 1;
                                })
                        )
                        .then(
                                l("composition").then(
                                        arg("timeline", StringArgumentType.word())
                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                                .then(
                                                        arg("composition_name", StringArgumentType.word())
                                                                .then(
                                                                        arg("composition_starttime", LongArgumentType.longArg(0))
                                                                        .then(
                                                                                builder_create_composition()
                                                                        )
                                                                )
                                                )
                                )
                        )
        );

        editorBuilder.then(
                l("delete")
                        .then(
                                l("timeline")
                                        .then(
                                                arg("timeline", StringArgumentType.word())
                                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                                        .executes(EditorSubcommand::delete_timeline)
                                        )
                        )
                        .then(
                                l("composition")
                                        .then(
                                                arg_timeline_composition(EditorSubcommand::delete_composition)
                                        )
                        )
        );

        editorBuilder.then(
                l("info")
                        .then(
                                arg("timeline", StringArgumentType.word())
                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                        .then(
                                                arg("composition", StringArgumentType.word())
                                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.COMPOSITION))
                                                        .then(
                                                                l("timevar")
                                                                        .then(
                                                                                arg("timevariable", StringArgumentType.word())
                                                                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_KEYS))
                                                                                        .executes(EditorSubcommand::info_timevariable_specific)
                                                                        )
                                                        )
                                                        .then(
                                                                l("constant")
                                                                        .then(
                                                                                arg("constant", StringArgumentType.word())
                                                                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.CONSTANT_KEYS))
                                                                                        .executes(EditorSubcommand::constant_get) // It's the same. Not doing the same thing two times
                                                                        )
                                                        )
                                                        .executes(EditorSubcommand::info_composition_specific)
                                        )
                                        .executes(EditorSubcommand::info_timeline_specific)
                )
                .executes(EditorSubcommand::info_cinematic)
        );

        editorBuilder.then(
            l("duration")
                    .then(
                            l("cinematic")
                                    .then(
                                            l("set")
                                                    .then(
                                                            arg("duration", LongArgumentType.longArg(1))
                                                                    .executes(EditorSubcommand::duration_cinematic_set)
                                                    )
                                    )
                                    .then(
                                            l("get").executes(EditorSubcommand::duration_cinematic_get)
                                    )
                                    .executes(EditorSubcommand::duration_cinematic_get)
                    )
                    .then(
                            l("composition")
                                    .then(
                                            arg_timeline_composition(
                                                    arg("duration", LongArgumentType.longArg(1))
                                                            .executes(EditorSubcommand::duration_composition_set),
                                                    EditorSubcommand::duration_composition_get
                                            )
                                    )
                    )
                    .executes(EditorSubcommand::duration_cinematic_get)
        );

        editorBuilder.then(
                l("constants")
                        .then(
                                arg_timeline_composition(
                                        arg("constant", StringArgumentType.word())
                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.CONSTANT_KEYS))
                                                .then(
                                                        arg_value(
                                                                l("set"),
                                                                EditorSubcommand::constant_set,
                                                                new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.CONSTANT_VALUE)
                                                        )
                                                )
                                                .then(
                                                        l("get")
                                                                .executes(EditorSubcommand::constant_get)
                                                )
                                                .executes(EditorSubcommand::constant_get)
                                )
                        )
        );

        var set_with_easing = l("easing").then(
                arg("easing", StringArgumentType.word())
                        .suggests(new EasingSuggestion()).then(
                                arg_value(
                                        arg("time", LongArgumentType.longArg(0)),
                                        EditorSubcommand::timevariable_set,
                                        new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_VALUE)
                                )
                        )
        );
        var set_normal = l("not_easing").then(
                arg_value(
                        arg("time", LongArgumentType.longArg(0)),
                        EditorSubcommand::timevariable_set,
                        new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_VALUE)
                )
        );
        editorBuilder.then(
                l("timevar")
                        .then(
                                arg_timeline_composition(
                                        arg("timevariable", StringArgumentType.word())
                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_KEYS))
                                                .then(
                                                        l("set")
                                                                .then(
                                                                        set_with_easing
                                                                )
                                                                .then(
                                                                        set_normal
                                                                )
                                                )
                                                .then(
                                                        l("add")
                                                                .then(
                                                                        set_with_easing
                                                                )
                                                                .then(
                                                                        set_normal
                                                                )
                                                )
                                                .then(
                                                        l("modify")
                                                                .then(
                                                                        l("easing")
                                                                                .then(
                                                                                        arg("time", LongArgumentType.longArg(0))
                                                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_POSITION))
                                                                                                .then(
                                                                                                        arg("easing", StringArgumentType.word())
                                                                                                                .suggests(new EasingSuggestion())
                                                                                                                .executes(EditorSubcommand::timevariable_modify_easing)
                                                                                                )
                                                                                )
                                                                )
                                                                .then(
                                                                        l("value")
                                                                                .then(
                                                                                        arg_value(
                                                                                                arg("time", LongArgumentType.longArg(0))
                                                                                                        .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_POSITION)),
                                                                                                EditorSubcommand::timevariable_modify_value,
                                                                                                new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_VALUE)
                                                                                        )
                                                                                )
                                                                )
                                                )
                                                .then(
                                                        l("get")
                                                                .then(
                                                                        arg("time", LongArgumentType.longArg(0))
                                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_POSITION))
                                                                                .executes(EditorSubcommand::timevariable_get_specific)
                                                                )
                                                                //.executes(EditorSubcommand::timevariable_get) Would be the same, not doing two times the same thing...
                                                                .executes(EditorSubcommand::info_timevariable_specific)
                                                )
                                                .then(
                                                        l("delete")
                                                                .then(
                                                                        arg("time", LongArgumentType.longArg(0))
                                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMEVARIABLE_POSITION))
                                                                                .executes(EditorSubcommand::timevariable_delete)
                                                                )
                                                )
                                                //.executes(EditorSubcommand::timevariable_get)
                                                .executes(EditorSubcommand::info_timevariable_specific)
                                )
                        )
        );

        editorBuilder.then(
                l("move")
                        .then(
                                l("composition")
                                        .then(
                                                arg_timeline_composition(
                                                        l("timeline")
                                                                .then(
                                                                        arg("newtimeline", StringArgumentType.word())
                                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                                                                .then(
                                                                                        arg("newtime", LongArgumentType.longArg(0))
                                                                                                .executes(EditorSubcommand::move_composition_timeline)
                                                                                )
                                                                                .executes(EditorSubcommand::move_composition_timeline)
                                                                ),
                                                        l("startTime")
                                                                .then(
                                                                        arg("newtime", LongArgumentType.longArg(0))
                                                                                .executes(EditorSubcommand::move_composition_time)
                                                                )
                                                )
                                        )
                        )
                        .then(
                                l("timeline")
                                        .then(
                                                l("up")
                                                        .then(
                                                                arg("positions", IntegerArgumentType.integer(1))
                                                                        .executes((ctx) -> EditorSubcommand.move_timeline(ctx, true))
                                                        )
                                        )
                                        .then(
                                                l("down")
                                                        .then(
                                                                arg("positions", IntegerArgumentType.integer(1))
                                                                        .executes((ctx) -> EditorSubcommand.move_timeline(ctx, false))
                                                        )
                                        )
                        )
        );

        editorBuilder.then(
                l("help")
                        .executes(ctx -> {
                            ctx.getSource().sendMessage(Text.of(SUBCOMMANDS));
                            return 1;
                        })
        );

        editorBuilder.executes(ctx -> {
            ctx.getSource().sendMessage(Text.of(SUBCOMMANDS));
            return 1;
        });

        return editorBuilder.build();
    }

    /////// BUILD COMMANDS FUNCTIONS ///////

    private static RequiredArgumentBuilder<ServerCommandSource, Long> builder_create_composition() {
        var builder = arg("composition_duration", LongArgumentType.longArg(1));

        for (ECompositionType ctype : ECompositionType.values()) {
            var typeBuilder = l(ctype.getName());

            if (ctype.hasSubtypes()) {
                for (ICompositionType subtype : ctype.getSubtypes()) {
                    typeBuilder.then(
                            l(subtype.getName())
                                    .executes((ctx) -> EditorSubcommand.create_composition(ctx, subtype))
                    );
                }
            } else {
                typeBuilder.executes((ctx) -> EditorSubcommand.create_composition(ctx, ctype));
            }

            builder.then(typeBuilder);
        }

        return builder;
    }

    /////// RUN COMMANDS FUNCTIONS ///////

    private static int create_composition(CommandContext<ServerCommandSource> ctx, ICompositionType subtype) throws CommandSyntaxException {
        var pairtc = CommandUtils.getTimeline(ctx);
        Cinematic cinematic = pairtc.getLeft();
        Timeline timeline = pairtc.getRight();

        String compositionName = StringArgumentType.getString(ctx, "composition_name");
        long startTime = LongArgumentType.getLong(ctx, "composition_starttime");
        long duration = LongArgumentType.getLong(ctx, "composition_duration");

        Composition compo = Composition.create(compositionName, subtype);

        try {
            timeline.add(compo, startTime, duration);
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aComposition created"));
        } catch (OverlapException | IllegalArgumentException e) {
            ctx.getSource().sendError(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
        }

        return 1;
    }

    private static int delete_timeline(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairct = CommandUtils.getTimeline(ctx);
        Cinematic cinematic = pairct.getLeft();
        Timeline timeline = pairct.getRight();

        if (cinematic.getCameraTimeline().getUuid().equals(timeline.getUuid())) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cYou can't delete the camera timeline"));
            return 1;
        }

        if (runDelete(player.getUuid(), String.valueOf(timeline.hashCode()))) {
            if (cinematic.removeTimeline(timeline)) {
                player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aTimeline deleted"));
            } else {
                player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cInvalid Timeline"));
            }
            System.gc();
            return 1;
        }

        ctx.getSource().sendError(Text.of(PolCinematicsCommand.PREFIX + "§cRe-enter the command to confirm that you want to delete the timeline."));
        DELETE_COOLDOWN.setOnCooldown(player.getUuid(), String.valueOf(timeline.hashCode()));
        return 1;
    }

    private static int delete_composition(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);
        Timeline timeline = pairtc.getLeft();
        WrappedComposition composition = pairtc.getRight();

        if (runDelete(player.getUuid(), String.valueOf(composition.hashCode()))) {
            if (timeline.remove(composition)) {
                player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aComposition deleted"));
            } else {
                player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cInvalid Composition"));
            }
            System.gc();
            return 1;
        }

        ctx.getSource().sendError(Text.of(PolCinematicsCommand.PREFIX + "§cRe-enter the command to confirm that you want to delete the composition."));
        DELETE_COOLDOWN.setOnCooldown(player.getUuid(), String.valueOf(composition.hashCode()));
        return 1;
    }

    private static int info_composition_specific(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);
        Timeline timeline = pairtc.getLeft();
        String timelineName = StringArgumentType.getString(ctx, "timeline");
        WrappedComposition wrappedComposition = pairtc.getRight();
        Composition composition = wrappedComposition.getComposition();

        // Info vars
        String name = composition.getName();
        UUID uuid = composition.getUuid();
        long duration = wrappedComposition.getDuration();
        long startTime = wrappedComposition.getStartTime();
        long endTime = wrappedComposition.getDuration();

        ECompositionType ctype = composition.getType();
        ICompositionType subtype = composition.getSubtype();

        // Format message
        StringBuilder message = new StringBuilder();
        message.append(BOTTOM_LINE).append("\n\n");
        message.append("§fName: §7").append(name).append("\n");
        message.append("§fUUID: §7").append(uuid).append("\n");
        message.append("§fDuration: §7").append(duration).append("\n");
        message.append("§fStart time: §7").append(startTime).append("\n");
        message.append("§fEnd time: §7").append(endTime).append("\n\n");

        message.append("§fType: §7").append(ctype.getName()).append("\n");
        message.append("§fSubtype: §7").append(subtype == null ? "None" : subtype.getName()).append("\n\n");


        message.append("§a§lConstants").append("\n");
        player.sendMessage(Text.of(message.toString()));
        for (Constant constant : composition.getCompositionConstants().getConstants()) {
            var editText = Text
                    .literal("[EDIT]")
                    .setStyle(
                            Style.EMPTY
                                    .withBold(true)
                                    .withColor(Formatting.DARK_AQUA)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce constants " + timelineName + " " + composition.getName() + " " + constant.getKey() + " set "))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§7Click to edit this constant")))
                    );
            player.sendMessage(Text.literal("§7§o(" + constant.getType().getName() + ") §r§f" + constant.getKey() + ": §7'" + constant.getValue() + "' ").append(editText).append(Text.of("\n§7  - " + constant.getDescription())));
        }

        player.sendMessage(Text.of("\n§b§lTime variables\n"));
        CompositionTimeVariables timeVariables = composition.getCompositionTimeVariables();
        for (String key : timeVariables.getKeys()) {
            TimeVariable timeVariable = timeVariables.getTimeVariables(key);
            var infotext = Text
                    .literal("[INFO]")
                    .setStyle(
                            Style.EMPTY
                                    .withBold(true)
                                    .withColor(Formatting.DARK_AQUA)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ce info " + timelineName + " " + composition.getName() + " timevar " + key))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§7Click to see more info about this timed variable.")))
                    );

            player.sendMessage(
                    Text
                            .literal("§7§o(" + timeVariable.getType().getName() + ") §r§7" + key + "§7- §f" + timeVariable.getKeyframeCount() + " keyframes ")
                            .append(infotext)
                            .append(Text.of("\n§7  - " + timeVariable.getDescription()))
            );
        }

        player.sendMessage(Text.of("\n" + BOTTOM_LINE));
        return 1;
    }

    private static int info_timevariable_specific(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);
        Timeline timeline = pairtc.getLeft();
        WrappedComposition wrappedComposition = pairtc.getRight();
        Composition composition = wrappedComposition.getComposition();

        String timelineName = StringArgumentType.getString(ctx, "timeline");

        String key = StringArgumentType.getString(ctx, "timevariable");
        TimeVariable timeVariable = composition.getCompositionTimeVariables().getTimeVariables(key);

        StringBuilder message = new StringBuilder(BOTTOM_LINE).append("\n\n");
        message.append("§fUUID: §7").append(timeVariable.getUuid()).append("\n");
        message.append("§fName: §7").append(timeVariable.getName()).append("\n");
        message.append("§fDescription: §7").append(timeVariable.getDescription()).append("\n");
        message.append("§fType: §7").append(timeVariable.getType().getName()).append("\n");
        message.append("§fKeyframe count: §7").append(timeVariable.getKeyframeCount()).append("\n\n");
        message.append("§b§lKeyframes");

        player.sendMessage(Text.literal(message.toString()).append(
                Text.literal(" [ADD KEYFRAME]")
                        .setStyle(Style.EMPTY
                                .withColor(Formatting.GREEN)
                                .withBold(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevar " + timeline.getUuid() + " " + composition.getUuid() + " " + timeVariable.getName() + " add "))
                        )
        ).append(Text.of("\n")));

        timeVariable.getAllKeyframes().forEach(keyframe -> {
            MutableText change = Text.literal(" [MODIFY]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_AQUA)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevar " + timeline.getUuid() + " " + composition.getUuid() + " " + timeVariable.getName() + " modify value " + keyframe.getTime() + " "))
            );
            MutableText easing = Text.literal(" [EASING]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.GOLD)
                            .withBold(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevar " + timeline.getUuid() + " " + composition.getUuid() + " " + timeVariable.getName() + " modify easing " + keyframe.getTime() + " "))
            );
            MutableText delete = Text.literal(" [DELETE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withBold(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevar " + timeline.getUuid() + " " + composition.getUuid() + " " + timeVariable.getName() + " delete " + keyframe.getTime()))
            );

            player.sendMessage(
                    Text
                            .literal("§f(" + keyframe.getTime() + ") §7- §f'" + keyframe.getValue().getValue() + "' §7- " + Easing.getName(keyframe.getEasing()))
                            .append(change)
                            .append(easing)
                            .append(delete)
            );
        });

        player.sendMessage(Text.of("\n" + BOTTOM_LINE));

        return 1;
    }

    private static int info_timeline_specific(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairct = CommandUtils.getTimeline(ctx);
        Timeline timeline = pairct.getRight();
        String timelineArg = StringArgumentType.getString(ctx, "timeline");

        StringBuilder message = new StringBuilder(BOTTOM_LINE).append("\n\n");
        message.append("§fTimeline UUID: §7").append(timeline.getUuid()).append("\n");
        message.append("§fTimeline Type: §7").append(timeline.getClass().getSimpleName()).append("\n");
        message.append("§fCompositions: §7").append(timeline.getWrappedCompositions().size()).append("\n");
        message.append("§fAllowed Composition Types: §7").append(Arrays.stream(timeline.getAllowedTypes()).map(ECompositionType::getName).collect(Collectors.joining(", "))).append("\n\n");
        message.append("§6§lCompositions: §7\n\n");
        player.sendMessage(Text.of(message.toString()));

        timeline.getWrappedCompositions().forEach(wc -> {
            Composition composition = wc.getComposition();

            MutableText info = Text.literal(" [INFO]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_AQUA)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ce info " + timeline.getUuid() + " " + wc.getUuid().toString()))
            );
            MutableText moveTimeline = Text.literal(" [MOVE TO TIMELINE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.GOLD)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce move composition " + timeline.getUuid() + " " + composition.getUuid() + " timeline "))
            );
            MutableText moveStarttime = Text.literal(" [MOVE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_PURPLE)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce move composition " + timeline.getUuid() + " " + composition.getUuid() + " startTime "))
            );
            MutableText duration = Text.literal(" [DURATION]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.GREEN)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce duration composition " + timelineArg + " " + wc.getUuid().toString() + " "))
            );
            MutableText delete = Text.literal(" [DELETE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce delete composition " + timeline.getUuid() + " " + wc.getUuid()))
            );

            player.sendMessage(
                    Text
                            .literal("§7§o(" + (composition.getSubtype() != null ? composition.getSubtype().getName() : composition.getType().getName()) + ") §r§f" + wc.getStartTime() + "/" + wc.getFinishTime() + "ms §7- §f" + composition.getName())
                            .append(info)
                            .append(moveStarttime)
                            .append(moveTimeline)
                            .append(duration)
                            .append(delete)
            );
        });

        player.sendMessage(Text.of("\n" + BOTTOM_LINE));
        return 1;
    }

    private static int info_cinematic(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        Cinematic cinematic = CommandUtils.getCinematic(ctx);

        String cinematicName = cinematic.getName();
        UUID cinematicUUID = cinematic.getUuid();
        Duration duration = cinematic.getDuration();
        ArrayList<Timeline> timelines = cinematic.getTimelines();

        StringBuilder message = new StringBuilder(BOTTOM_LINE);
        message.append("\n\n").append("§fName: §7").append(cinematicName).append("\n");
        message.append("§fUUID: §7").append(cinematicUUID.toString()).append("\n");
        message.append("§fDuration: §7").append(duration.toString()).append("\n");

        player.sendMessage(Text.of(message.toString()));

        timelines.forEach(timeline -> {
            MutableText info = Text.literal(" [INFO] ").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_AQUA)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ce info " + timeline.getUuid()))
            );
            MutableText delete = Text.literal("[DELETE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce delete timeline " + timeline.getUuid()))
            );

            player.sendMessage(
                    Text
                            .literal("§7-  §f" + (cinematic.getCameraTimeline().getUuid().equals(timeline.getUuid()) ? "Camera" : timelines.indexOf(timeline)))
                            .append(info)
                            .append(delete)
            );
        });

        player.sendMessage(Text.of("\n" + BOTTOM_LINE));
        return 1;
    }

    private static int duration_cinematic_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        Cinematic cinematic = CommandUtils.getCinematic(ctx);
        
        long newDuration = LongArgumentType.getLong(ctx, "duration");
        cinematic.setDuration(newDuration);
        
        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aCinematic duration has been updated to §f" + newDuration + " §amilliseconds."));
        return 1;
    }

    private static int duration_cinematic_get(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        Cinematic cinematic = CommandUtils.getCinematic(ctx);

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7Cinematic duration is §f" + cinematic.getDuration().toMillis() + " §7milliseconds."));
        return 1;
    }

    private static int duration_composition_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);
        Timeline timeline = pairtc.getLeft();
        WrappedComposition wc = pairtc.getRight();
        long newDuration = LongArgumentType.getLong(ctx, "duration");

        try {
            timeline.changeDuration(wc, newDuration);
        } catch (OverlapException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
            return 1;
        }

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aComposition time has been updated to §f" + newDuration + " §amilliseconds."));
        return 1;
    }

    private static int duration_composition_get(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aComposition time is §f" + pairtc.getRight().getDuration() + " §amilliseconds."));
        return 1;
    }

    private static void trysetting(Composition compo, String key, Object value) {
        try {
            Constant constt = compo.getConstant(key);
            constt.setValue(value);
        } catch (Exception ignore) {}
    }

    private static int constant_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var paircom = CommandUtils.getComposition(ctx);
        var pairkp = CommandUtils.getConstant(ctx);
        boolean isPos = CameraUtils.containsPositionArgument(paircom.getRight().getComposition()) && pairkp.getLeft().equalsIgnoreCase("POSITION");

        try {
            Object value = isPos ? getValueCameraFrame(ctx) : getValue(ctx, pairkp.getRight().getType());
            if (isPos) {
                CameraFrame cam = (CameraFrame) value;
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.X_KEY, cam.getX());
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.Y_KEY, cam.getY());
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.Z_KEY, cam.getZ());
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.PITCH_KEY, (double) cam.getPitch());
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.YAW_KEY, (double) cam.getYaw());
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.ROLL_KEY, (double) cam.getRoll());
            } else {
                pairkp.getRight().setValue(value);
            }
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§f" + pairkp.getLeft() + " §7has been set to §f" + value + "§7."));
        } catch (InvalidValueException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cThere's an issue that shouldn't have happened. Open an issue with your server logs."));
            e.printStackTrace();
        }
        return 1;
    }

    private static int constant_get(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairkp = CommandUtils.getConstant(ctx);

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§f" + pairkp.getLeft() + "§7 is §f" + pairkp.getRight().getValue() + "§7."));
        return 1;
    }

    private static void trysetting(Composition compo, String key, Object value, long time) {
        try {
            TimeVariable tv = compo.getTimeVariable(key);
            tv.setKeyframe(time, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int timevariable_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var paircom = CommandUtils.getComposition(ctx);
        var pairkattr = CommandUtils.getTimeVariable(ctx);
        long time = LongArgumentType.getLong(ctx, "time");
        Easing easing = Easing.EASE_INOUT_QUAD;

        boolean isPos = CameraUtils.containsPositionArgument(paircom.getRight().getComposition()) && pairkattr.getLeft().equalsIgnoreCase("POSITION");

        try {
            String easingName = StringArgumentType.getString(ctx, "easing");
            easing = Easing.fromName(easingName.toUpperCase());
            if (easing == null)
                throw new InvalidValueException("§cInvalid easing name. ");
        } catch (InvalidValueException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + e.getMessage()));
            return 1;
        } catch (Exception ignore) {}

        try {
            Object value = isPos ? getValueCameraFrame(ctx) : getValue(ctx, pairkattr.getRight().getType());
            if (isPos) {
                CameraFrame cam = (CameraFrame) value;
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.X_KEY, cam.getX(), time);
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.Y_KEY, cam.getY(), time);
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.Z_KEY, cam.getZ(), time);
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.PITCH_KEY, (double) cam.getPitch(), time);
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.YAW_KEY, (double) cam.getYaw(), time);
                trysetting(paircom.getRight().getComposition(), DeclarationUtils.ROLL_KEY, (double) cam.getRoll(), time);
            } else {
                pairkattr.getRight().setKeyframe(time, value, easing);
            }
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§f" + pairkattr.getLeft() + " §7has been set to §f" + value + "§7."));
        } catch (InvalidValueException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
            return 1;
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cThere's an issue that shouldn't have happened. Open an issue with your server logs."));
            e.printStackTrace();
            return 1;
        }
        return 1;
    }

    private static int timevariable_get_specific(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairattrk = CommandUtils.getKeyframe(ctx);

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§7" + pairattrk.getLeft() + " is §f" + pairattrk.getRight().getValue() + " §7with easing §e" + Easing.getName(pairattrk.getRight().getEasing()) + "§7."));
        return 1;
    }

    private static int timevariable_modify_easing(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairattrk = CommandUtils.getKeyframe(ctx); // Maybe catch and send PolCinematicsCommand.PREFIX + "§cThere's no keyframe at this time." if null?
        Easing easing = Easing.fromName(StringArgumentType.getString(ctx, "easing").toUpperCase());

        pairattrk.getRight().setEasing(easing);
        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aEasing has been updated to §f" + Easing.getName(easing) + "§a."));
        return 1;
    }

    private static int timevariable_modify_value(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairattrk = CommandUtils.getKeyframe(ctx);

        Object value;
        try {
            value = getValue(ctx, pairattrk.getRight().getType());
        } catch (InvalidValueException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
            return 1;
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cThere's an issue that shouldn't have happened. Open an issue with your server logs."));
            e.printStackTrace();
            return 1;
        }

        pairattrk.getRight().getValue().setValue(value);
        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aValue has been updated to §f" + value + "§a."));
        return 1;
    }

    private static int timevariable_delete(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var timeVariable = CommandUtils.getTimeVariable(ctx);
        var keyframe = CommandUtils.getKeyframe(ctx);

        try {
            timeVariable.getRight().removeExactKeyframe(keyframe.getRight().getTime());
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aKeyframe has been deleted."));
        } catch (DeleteKeyframeException e) {
            ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
        }

        return 1;
    }

    private static int move_composition_timeline(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        Cinematic cinematic = CommandUtils.getCinematic(ctx);
        var pairtc = CommandUtils.getComposition(ctx);
        Timeline newtimeline = cinematic.resolveTimeline(StringArgumentType.getString(ctx, "newtimeline"));
        long newtime = pairtc.getRight().getStartTime();

        try {
            newtime = LongArgumentType.getLong(ctx, "newtime");
        } catch (Exception ignore) {}

        if (newtimeline == null)
            throw PolCinematicsCommand.INVALID_TIMELINE.create();

        try {
            cinematic.moveComposition(pairtc.getRight(), pairtc.getLeft(), newtimeline, newtime);
        } catch (OverlapException | IllegalArgumentException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + e.getMessage()));
            return 1;
        }

        player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aTimeline has been moved."));
        return 1;
    }

    private static int move_composition_time(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairtc = CommandUtils.getComposition(ctx);
        Timeline timeline = pairtc.getLeft();
        long newtime = LongArgumentType.getLong(ctx, "newtime");

        try {
            timeline.move(pairtc.getRight().getUuid(), newtime);
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aTimeline has been moved."));
        } catch (OverlapException e) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + e.getMessage()));
        }

        return 1;
    }

    private static int move_timeline(CommandContext<ServerCommandSource> ctx, boolean isUp) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairct = CommandUtils.getTimeline(ctx);
        int positions = IntegerArgumentType.getInteger(ctx, "positions");

        Cinematic cinematic = pairct.getLeft();
        if (!cinematic.canMove(pairct.getRight(), positions, isUp)) {
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§cYou can't move this timeline here."));
        } else {
            cinematic.moveTimeline(pairct.getRight(), positions, isUp);
            player.sendMessage(Text.of(PolCinematicsCommand.PREFIX + "§aTimeline has been moved."));
        }

        return 1;
    }

    /////// OTHER FUNCTIONS ///////

    private static CameraFrame getValueCameraFrame(CommandContext<ServerCommandSource> ctx) throws InvalidValueException, CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        try {
            float roll = Float.parseFloat(StringArgumentType.getString(ctx, "stringValue"));
            return new CameraFrame(player.getX(), player.getY(), player.getZ(), player.getPitch(), player.getYaw(), roll);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid roll value");
        } catch (Exception ignore) {}

        try {
            Vec3d pos = Vec3ArgumentType.getVec3(ctx, "vec3Value");
            Float pitch = null;
            Float yaw = null;
            Float roll = 0f;

            try {
                pitch = FloatArgumentType.getFloat(ctx, "pitch");
                yaw = FloatArgumentType.getFloat(ctx, "yaw");
                roll = FloatArgumentType.getFloat(ctx, "roll");
            } catch (Exception ignore) {}

            if (pitch == null) {
                pitch = player.getPitch();
            }

            if (yaw == null) {
                yaw = player.getYaw();
            }

            return new CameraFrame(pos.x, pos.y, pos.z, pitch, yaw, roll);
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().contains("No such argument")) {
                throw new InvalidValueException("Invalid position");
            }
        }

        return new CameraFrame(player.getX(), player.getY(), player.getZ(), player.getPitch(), player.getYaw(), 0f);
    }

    private static Object getValue(CommandContext<ServerCommandSource> ctx, EValueType valueType) throws InvalidValueException {
        /*
        stringValue
        entityValue
        entitiesValue
        vec3Value
         */

        try {
            switch (valueType) {
                case DOUBLE -> {
                    try {
                        return Double.parseDouble(StringArgumentType.getString(ctx, "stringValue"));
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid double value");
                    }
                }
                case INTEGER -> {
                    try {
                        return Integer.parseInt(StringArgumentType.getString(ctx, "stringValue"));
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid integer value");
                    }
                }
                case BOOLEAN -> {
                    try {
                        String value = StringArgumentType.getString(ctx, "stringValue");
                        if (value.equals("true") || value.equals("false")) {
                            return Boolean.parseBoolean(value);
                        } else {
                            throw new IllegalArgumentException("Invalid boolean value");
                        }
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid boolean value");
                    }
                }
                case COLOR -> {
                    String colorHex;
                    try {
                        colorHex = StringArgumentType.getString(ctx, "stringValue");
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid color");
                    }

                    if (!colorHex.startsWith("#")) {
                        throw new InvalidValueException("Invalid color. Color must start with #");
                    }

                    try {
                        Color color = Color.decode(colorHex);
                        return ColorUtils.getColor(color);
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException("Invalid color. Color must be in hex format");
                    }
                }
                case STRING -> {
                    try {
                        return StringArgumentType.getString(ctx, "stringValue");
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid string value. If you think it is still a string, wrap it with \"<your string>\"");
                    }
                }
                default -> throw new InvalidCommandValueException("Invalid value type");
            }
        } catch (Exception e) {
            throw new InvalidCommandValueException(e);
        }
    }

    private static boolean runDelete(UUID uuid, String hash) {
        if (DELETE_COOLDOWN.isOnCooldown(uuid, hash)) {
            DELETE_COOLDOWN.removeCooldown(uuid);
            return true;
        }

        return false;
    }

}
