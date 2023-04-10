package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.commands.suggetions.CinematicLoadedSuggestion;
import dev.polv.polcinematics.commands.suggetions.CinematicTimelineSuggestion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EditorSubcommand {

    public static LiteralCommandNode<ServerCommandSource> build() {
        RequiredArgumentBuilder<ServerCommandSource, String> editorBuilder = CommandManager.argument("cinematicname", StringArgumentType.string()).suggests(new CinematicLoadedSuggestion());

        editorBuilder.then(TimelineSubcommands.build());

        return CommandManager.literal("editor").then(editorBuilder).build();
    }

    private static class TimelineSubcommands {

        public static LiteralArgumentBuilder<ServerCommandSource> build() {
            RequiredArgumentBuilder<ServerCommandSource, String> timelineBuilder = CommandManager
                    .argument("timeline", StringArgumentType.string())
                    .suggests(new CinematicTimelineSuggestion());

            timelineBuilder.then(CommandManager.literal("list").executes(TimelineSubcommands::list));

            return CommandManager.literal("timeline").then(timelineBuilder.build());
        }

        private static int list(CommandContext<ServerCommandSource> context) {
            String cinematicName = StringArgumentType.getString(context, "cinematicname");
            Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(cinematicName);
            String timelineName = StringArgumentType.getString(context, "timeline");
            Timeline timeline = timelineName.equals("camera") ? cinematic.getCameraTimeline() : cinematic.getTimeline(Integer.parseInt(timelineName));

            timeline.getWrappedCompositions().forEach(wc -> {
                String message = wc.toString();
                context.getSource().sendMessage(Text.literal(message));
            });

            return 1;
        }

    }

}
