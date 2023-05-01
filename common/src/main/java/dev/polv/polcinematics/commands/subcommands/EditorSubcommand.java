package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraRot;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.helpers.CommandCooldown;
import dev.polv.polcinematics.commands.suggetions.*;
import dev.polv.polcinematics.exception.InvalidCommandValueException;
import dev.polv.polcinematics.utils.ChatUtils;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.util.UUID;

public class EditorSubcommand {

    private static final String SUBCOMMANDS = ChatUtils.formatHelpMessage(
            "create", "Create a new timeline or composition",
            "delete", "Delete a timeline or composition",
            "info", "Get information about a timeline or composition",
            "duration", "Get or set the duration of a timeline or composition",
            "property", "Get or set the value of a property of a timeline or composition",
            "attribute", "Get or set the value of an attribute of a timeline or composition",
            "help", "Display this help message"
    );

    private static final CommandCooldown deleteCooldown = new CommandCooldown(Duration.ofSeconds(15));

    private static LiteralArgumentBuilder<ServerCommandSource> l(String name) {
        return CommandManager.literal(name);
    }

    private static <T> RequiredArgumentBuilder<ServerCommandSource, T> arg(String name, ArgumentType<T> argumentType) {
        return CommandManager.argument(name, argumentType);
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
                                l("timeline").executes((context) -> {
                                    Cinematic cinematic = getCinematic(context.getSource().getPlayer());

                                    Timeline timeline = cinematic.addTimeline();

                                    context.getSource().sendMessage(Text.literal(PolCinematicsCommand.PREFIX + "Timeline " + cinematic.getTimelineCount() + " created"));
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
                                                        .executes(EditorSubcommand::info_composition_specific)
                                        )
                                        .executes(EditorSubcommand::info_timeline_specific)
                )
                .executes(EditorSubcommand::info_timeline_all)
        );

        editorBuilder.then(
            l("duration")
                    .then(
                            l("cinematic")
                                    .then(
                                            arg("duration", LongArgumentType.longArg(1))
                                                    .executes(EditorSubcommand::duration_cinematic_set)

                                    )
                                    .executes(EditorSubcommand::duration_composition_get)
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
                    .executes(EditorSubcommand::duration)
        );

        editorBuilder.then(
                l("property")
                        .then(
                                arg_timeline_composition(
                                        arg("property", StringArgumentType.word())
                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.PROPERTY_KEYS))
                                                .then(
                                                        l("set")
                                                                .then(
                                                                        arg("entityValue", EntityArgumentType.entity())
                                                                                .executes(EditorSubcommand::property_set)
                                                                ).then(
                                                                        arg("entitiesValue", EntityArgumentType.entities())
                                                                                .executes(EditorSubcommand::property_set)
                                                                ).then(
                                                                        arg("stringValue", StringArgumentType.greedyString())
                                                                                .executes(EditorSubcommand::property_set)
                                                                ).then(
                                                                        arg("intValue", IntegerArgumentType.integer())
                                                                                .executes(EditorSubcommand::property_set)
                                                                ).then(
                                                                        arg("floatValue", DoubleArgumentType.doubleArg())
                                                                                .executes(EditorSubcommand::property_set)
                                                                ).then(
                                                                        arg("vec3Value", Vec3ArgumentType.vec3())
                                                                                .executes(EditorSubcommand::property_set)
                                                                )
                                                                .executes(EditorSubcommand::property_set)
                                                )
                                                .then(
                                                        l("get")
                                                                .executes(EditorSubcommand::property_get)
                                                )
                                                .executes(EditorSubcommand::property_get)
                                )
                        )
        );

        editorBuilder.then(
                l("attribute")
                        .then(
                                arg_timeline_composition(
                                        arg("attribute", StringArgumentType.word())
                                                .suggests(new CinematicThingsSuggestion(CinematicThingsSuggestion.SuggestionType.ATTRIBUTE_KEYS))
                                                .then(
                                                        l("set")
                                                                .then(
                                                                        arg("entityValue", EntityArgumentType.entity())
                                                                                .then(
                                                                                        arg("time", LongArgumentType.longArg(0)).then(
                                                                                                arg("easing", StringArgumentType.word())
                                                                                                        .suggests(new EasingSuggestion())
                                                                                                        .then(
                                                                                                                arg("stringValue", StringArgumentType.greedyString())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        ).then(
                                                                                                                arg("entityValue", EntityArgumentType.entity())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        ).then(
                                                                                                                arg("entitiesValue", EntityArgumentType.entities())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        ).then(
                                                                                                                arg("intValue", IntegerArgumentType.integer())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        ).then(
                                                                                                                arg("floatValue", DoubleArgumentType.doubleArg())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        ).then(
                                                                                                                arg("vec3Value", Vec3ArgumentType.vec3())
                                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                                        )
                                                                                                        .executes(EditorSubcommand::attribute_set)
                                                                                        )
                                                                                )
                                                                )
                                                )
                                                .then(
                                                        l("get")
                                                                .executes(EditorSubcommand::attribute_get)
                                                )
                                                .executes(EditorSubcommand::attribute_get)
                                )
                        )
        );

        editorBuilder.then(
                l("help")
                        .executes(context -> {
                            context.getSource().sendMessage(Text.of(SUBCOMMANDS));
                            return 1;
                        })
        );

        return editorBuilder.build();
    }

    /////// BUILD COMMANDS FUNCTIONS ///////

    private static RequiredArgumentBuilder<ServerCommandSource, Long> builder_create_composition() {
        var builder = arg("composition_endtime", LongArgumentType.longArg(1));

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

    private static int create_composition(CommandContext<ServerCommandSource> ctx, ECompositionType ctype) {
        return 1;
    }

    private static int delete_timeline(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int delete_composition(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int info_composition_specific(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int info_timeline_specific(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int info_timeline_all(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int duration_cinematic_set(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int duration_composition_set(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int duration(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int duration_composition_get(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int property_set(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int property_get(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int attribute_set(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        return 1;
    }

    private static int attribute_get(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        return 1;
    }

    /////// OTHER FUNCTIONS ///////

    private static Cinematic getCinematic(ServerPlayerEntity player) throws CommandSyntaxException {
        Cinematic cinematic = ManagerSubcommand.getSelectedCinematic(player);

        if (cinematic == null) {
            throw PolCinematicsCommand.CINEMATIC_NOT_SELECTED.create();
        }

        return cinematic;
    }

    private static Pair<Cinematic, Timeline> getTimeline(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(context.getSource().getPlayer());

        String timelineName = StringArgumentType.getString(context, "timeline");
        Timeline timeline = timelineName.equals("camera") ? cinematic.getCameraTimeline() : cinematic.getTimeline(Integer.parseInt(timelineName));

        if (timeline == null) {
            throw PolCinematicsCommand.INVALID_TIMELINE.create();
        }

        return new Pair<>(cinematic, timeline);
    }

    private static Pair<Timeline, Timeline.WrappedComposition> getComposition(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Cinematic cinematic = getCinematic(context.getSource().getPlayer());

        String stringUUID = StringArgumentType.getString(context, "composition");
        stringUUID = stringUUID.substring(stringUUID.length() - 37, stringUUID.length() - 1);

        UUID compoUUID;
        try {
            compoUUID = UUID.fromString(stringUUID);
        } catch (IllegalArgumentException e) {
            throw PolCinematicsCommand.INVALID_UUID.create();
        }

        var pair = cinematic.getTimelineAndWrappedComposition(compoUUID);

        if (pair == null)
            throw PolCinematicsCommand.INVALID_COMPOSITION.create();

        return pair;
    }

    private static Object getValue(CommandContext<ServerCommandSource> ctx, EValueType valueType) throws InvalidCommandValueException {
        String value = StringArgumentType.getString(ctx, "value");

        try {
            switch (valueType) {
                case CAMERAPOS -> {
                    Vec3d vec3d = Vec3ArgumentType.getVec3(ctx, "value");
                    return new CameraPos(vec3d.x, vec3d.y, vec3d.z);
                }
                case CAMERAROT -> {
                    // value should be three floats separated by spaces
                    String[] values = value.split(" ");
                    if (values.length != 3) {
                        throw new InvalidCommandValueException("Invalid number of arguments for CameraRot");
                    }

                    return new CameraRot(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
                }
                case DOUBLE -> {
                    return Double.parseDouble(value);
                }
                case INTEGER -> {
                    return Integer.parseInt(value);
                }
                case BOOLEAN -> {
                    value = value.toLowerCase();
                    if (value.equals("true") || value.equals("false")) {
                        return Boolean.parseBoolean(value);
                    } else {
                        throw new InvalidCommandValueException("Invalid boolean value");
                    }
                }
                case COLOR -> {
                    return ColorUtils.getColor(Color.decode(value));
                }
                case STRING -> {
                    return value;
                }
                default -> throw new InvalidCommandValueException("Invalid value type");
            }
        } catch (Exception e) {
            throw new InvalidCommandValueException(e);
        }
    }

    private static boolean runDelete(UUID uuid) {
        if (deleteCooldown.isOnCooldown(uuid)) {
            deleteCooldown.removeCooldown(uuid);
            return true;
        }

        return false;
    }

}
