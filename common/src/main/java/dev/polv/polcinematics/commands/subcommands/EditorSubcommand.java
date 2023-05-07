package dev.polv.polcinematics.commands.subcommands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import dev.polv.polcinematics.cinematic.compositions.values.constants.Constant;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.TimeVariable;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.CompositionTimeVariables;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraRot;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.helpers.CommandCooldownHash;
import dev.polv.polcinematics.commands.suggetions.CinematicThingsSuggestion;
import dev.polv.polcinematics.commands.suggetions.CompositionTypeSuggestion;
import dev.polv.polcinematics.commands.suggetions.EasingSuggestion;
import dev.polv.polcinematics.exception.InvalidCommandValueException;
import dev.polv.polcinematics.exception.InvalidValueException;
import dev.polv.polcinematics.exception.OverlapException;
import dev.polv.polcinematics.utils.ChatUtils;
import dev.polv.polcinematics.utils.ColorUtils;
import dev.polv.polcinematics.utils.CommandUtils;
import dev.polv.polcinematics.utils.EnumUtils;
import dev.polv.polcinematics.utils.math.Easing;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class EditorSubcommand {

    private static final String SUBCOMMANDS = ChatUtils.formatHelpMessage(
            "create", "Create a new timeline or composition",
            "delete", "Delete a timeline or composition",
            "info", "Get information about a timeline or composition",
            "duration", "Get or set the duration of a timeline or composition",
            "constants", "Get or set the value of a constant of a timeline or composition",
            "timevar", "Get or set the value of a timed variable of a timeline or composition",
            "help", "Display this help message"
    );

    private static final String BOTTOM_LINE = "§8§l=====================================";


    private static final CommandCooldownHash DELETE_COOLDOWN = new CommandCooldownHash(Duration.ofSeconds(15));

    private static LiteralArgumentBuilder<ServerCommandSource> l(String name) {
        return CommandManager.literal(name);
    }

    private static <T> RequiredArgumentBuilder<ServerCommandSource, T> arg(String name, ArgumentType<T> argumentType) {
        return CommandManager.argument(name, argumentType);
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

    private static RequiredArgumentBuilder<ServerCommandSource, String> arg_timeline_composition(ArgumentBuilder<ServerCommandSource, ?> builder) {
        return arg("timeline", StringArgumentType.word())
                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                .then(
                        arg("composition", StringArgumentType.word())
                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.COMPOSITION))
                                .then(builder)
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
                    .executes(EditorSubcommand::duration_composition_get)
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
                                                        arg("timeline", StringArgumentType.word())
                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                                                .then(
                                                                        l("timeline")
                                                                                .then(
                                                                                        arg("newtimeline", StringArgumentType.word())
                                                                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.TIMELINE))
                                                                                                .then(
                                                                                                        arg("newtime", LongArgumentType.longArg(0))
                                                                                                                .executes(EditorSubcommand::move_composition_timeline)
                                                                                                )
                                                                                                .executes(EditorSubcommand::move_composition_timeline)
                                                                                )
                                                                )
                                                                .then(
                                                                        l("startTime").then(
                                                                                arg("newtime", LongArgumentType.longArg(0))
                                                                                        .executes(EditorSubcommand::move_composition_time)
                                                                        )
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

        return editorBuilder.build();
    }

    /////// BUILD COMMANDS FUNCTIONS ///////

    private static RequiredArgumentBuilder<ServerCommandSource, Long> builder_create_composition() {
        var builder = arg("composition_duration", LongArgumentType.longArg(1));

        for (ECompositionType ctype : ECompositionType.values()) {
            var typeBuilder = l(ctype.getName());

            if (ctype.hasSubtypes()) {
                typeBuilder.then(arg("composition_subtype", StringArgumentType.word())
                        .suggests(new CompositionTypeSuggestion(ctype.getSubtypes()))
                        .executes((ctx) -> EditorSubcommand.create_composition(ctx, ctype)));
            } else {
                typeBuilder.executes((ctx) -> EditorSubcommand.create_composition(ctx, ctype));
            }

            builder.then(typeBuilder);
        }

        return builder;
    }

    /////// RUN COMMANDS FUNCTIONS ///////

    private static int create_composition(CommandContext<ServerCommandSource> ctx, ECompositionType ctype) throws CommandSyntaxException {
        var pairtc = CommandUtils.getTimeline(ctx);
        Cinematic cinematic = pairtc.getLeft();
        Timeline timeline = pairtc.getRight();

        String compositionName = StringArgumentType.getString(ctx, "composition_name");
        long startTime = LongArgumentType.getLong(ctx, "composition_starttime");
        long duration = LongArgumentType.getLong(ctx, "composition_duration");

        ICompositionType subtype = null;
        if (ctype.hasSubtypes()) {
            try {
                String subtypename = StringArgumentType.getString(ctx, "composition_subtype");
                subtype = EnumUtils.findSubtype(ctype, subtypename);
            } catch (IllegalArgumentException ignore) {} // subtype will be null

            if (subtype == null) {
                ctx.getSource().sendError(Text.of(PolCinematicsCommand.PREFIX + "§cYou need to specify a valid subtype for this composition type."));
                return 1;
            }
        }

        Composition compo = Composition.create(compositionName, subtype);

        try {
            timeline.add(compo, startTime, duration);
        } catch (OverlapException e) {
            ctx.getSource().sendError(Text.of(PolCinematicsCommand.PREFIX + "§c" + e.getMessage()));
        }

        return 1;
    }

    private static int delete_timeline(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairct = CommandUtils.getTimeline(ctx);
        Cinematic cinematic = pairct.getLeft();
        Timeline timeline = pairct.getRight();

        if (runDelete(player.getUuid(), String.valueOf(timeline.hashCode()))) {
            cinematic.removeTimeline(timeline);
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
            timeline.remove(composition);
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
            player.sendMessage(Text.literal("§7§o(" + constant.getType().getName() + ") §r§f" + constant.getKey() + ": §7" + constant.getValue() + " ").append(editText));
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
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ce info " + timelineName + " " + composition.getName() + " timed variable " + key))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§7Click to see more info about this timed variable.")))
                    );

            player.sendMessage(
                    Text
                            .literal("§7§o(" + timeVariable.getType().getName() + ") §r§7" + key + "§7- §6" + timeVariable.getKeyframeCount() + " §fkeyframes ")
                            .append(infotext)
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

        timeVariable.getAllKeyframes().forEach(keyframe -> {
            MutableText change = Text.literal(" [MODIFY]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_AQUA)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevariable " + timelineName + " " + composition.getUuid() + " " + timeVariable.getName() + " modify value " + keyframe.getTime() + " "))
            );
            MutableText easing = Text.literal(" [EASING]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.GOLD)
                            .withBold(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevariable " + timelineName + " " + composition.getUuid() + " " + timeVariable.getName() + " modify easing " + keyframe.getTime() + " "))
            );
            MutableText delete = Text.literal(" [DELETE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withBold(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce timevariable " + timelineName + " " + composition.getUuid() + " " + timeVariable.getName() + " delete " + keyframe.getTime()))
            );

            player.sendMessage(
                    Text
                            .literal("§f" + keyframe.getTime() + " §7- §6" + keyframe.getValue().getValue() + " §8- " + Easing.getName(keyframe.getEasing()))
                            .append(change)
                            .append(easing)
                            .append(delete)
            );
        });

        return 1;
    }

    private static int info_timeline_specific(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairct = CommandUtils.getTimeline(ctx);
        Timeline timeline = pairct.getRight();
        String timelineArg = StringArgumentType.getString(ctx, "timeline");

        timeline.getWrappedCompositions().forEach(wc -> {
            Composition composition = wc.getComposition();

            MutableText info = Text.literal(" [INFO]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.DARK_AQUA)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ce info " + timelineArg + " " + wc.getUuid().toString()))
            );
            MutableText duration = Text.literal(" [DURATION] ").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.GREEN)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce duration composition " + timelineArg + " " + wc.getUuid().toString() + " "))
            );
            MutableText delete = Text.literal("[DELETE]").setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ce delete composition " + wc.getUuid()))
            );

            player.sendMessage(
                    Text
                            .literal("§f" + wc.getStartTime() + "ms -> " + wc.getFinishTime() + "ms §7- §e" + composition.getName() + " §7- §6" + (composition.getSubtype() != null ? composition.getSubtype().getName() : composition.getType().getName()))
                            .append(info)
                            .append(duration)
                            .append(delete)
            );
        });

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
                            .literal("§7-  §f" + timeline)
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

    private static int constant_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairkp = CommandUtils.getConstant(ctx);

        try {
            Object value = getValue(ctx, pairkp.getRight().getType());
            pairkp.getRight().setValue(value);
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

    private static int timevariable_set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        var pairkattr = CommandUtils.getTimeVariable(ctx);
        long time = LongArgumentType.getLong(ctx, "time");
        Easing easing = Easing.EASE_INOUT_QUAD;

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
            Object value = getValue(ctx, pairkattr.getRight().getType());
            pairkattr.getRight().setKeyframe(time, value, easing);
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
        } catch (OverlapException e) {
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

    private static Object getValue(CommandContext<ServerCommandSource> ctx, EValueType valueType) throws InvalidValueException {
        /*
        stringValue
        entityValue
        entitiesValue
        vec3Value
         */

        try {
            switch (valueType) {
                case CAMERAPOS -> {
                    try {
                        Vec3d vec3d = Vec3ArgumentType.getVec3(ctx, "vec3Value");
                        return new CameraPos(vec3d.x, vec3d.y, vec3d.z);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid position");
                    }
                }
                case CAMERAROT -> {
                    try {
                        Vec3d vec3d = Vec3ArgumentType.getVec3(ctx, "vec3Value");
                        return new CameraRot((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
                    } catch (IllegalArgumentException ignore) {}

                    String value;
                    try {
                        value = StringArgumentType.getString(ctx, "stringValue");
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid rotation");
                    }

                    String[] values = value.split(" ");
                    if (values.length != 3) {
                        throw new InvalidValueException("Invalid rotation. There's only three rotations: pitch, yaw, roll");
                    }

                    float[] rotations;
                    try {
                        rotations = new float[]{
                                Float.parseFloat(values[0]),
                                Float.parseFloat(values[1]),
                                Float.parseFloat(values[2])
                        };
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException("Invalid number format.");
                    }

                    return new CameraRot(rotations[0], rotations[1], rotations[2]);
                }
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
