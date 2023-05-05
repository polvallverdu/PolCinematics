package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.groups.PlayerGroup;
import dev.polv.polcinematics.commands.suggetions.GroupSuggestion;
import dev.polv.polcinematics.exception.NameException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GroupSubcommand {

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> groupBuilder = CommandManager.literal("groups");

        groupBuilder
                .then(
                        CommandManager.literal("create")
                                .then(
                                        CommandManager.argument("name", StringArgumentType.word())
                                                .then(
                                                        CommandManager.argument("selector", StringArgumentType.greedyString())
                                                                .suggests((context, builder) -> EntityArgumentType.players().listSuggestions(context, builder))
                                                                .executes((ctx) -> {
                                                                    String selectorString = StringArgumentType.getString(ctx, "selector");
                                                                    try {
                                                                        new EntitySelectorReader(new StringReader(selectorString)).read();
                                                                    } catch (CommandSyntaxException e) {
                                                                        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Invalid selector"));
                                                                    }

                                                                    String name = StringArgumentType.getString(ctx, "name");
                                                                    try {
                                                                        PlayerGroup group = PolCinematics.getGroupManager().createGroup(name, selectorString);
                                                                        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + group.getName() + " created. Currently it selects " + group.getPlayers(ctx.getSource()).size() + " online players."));
                                                                    } catch (NameException e) {
                                                                        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + e.getMessage()));
                                                                    }
                                                                    return 1;
                                                                })
                                                )
                                )
                )
                .then(
                        CommandManager.literal("list")
                                .executes((ctx) -> {
                                    ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Groups:"));
                                    PolCinematics.getGroupManager().getGroups().forEach((group) -> {
                                        ctx.getSource().sendMessage(Text.of(group.getName() + " (" + group.getPlayers(ctx.getSource()).size() + " players)"));
                                    });
                                    return 1;
                                })
                )
                .then(
                        CommandManager.literal("delete")
                                .then(
                                        CommandManager.argument("name", StringArgumentType.word())
                                                .suggests(new GroupSuggestion())
                                                .executes((ctx) -> {
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    PlayerGroup group = PolCinematics.getGroupManager().resolveGroup(name);
                                                    if (group == null) {
                                                        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + name + " not found."));
                                                        return 1;
                                                    }
                                                    PolCinematics.getGroupManager().deleteGroup(group);
                                                    ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + name + " deleted."));
                                                    return 1;
                                                })
                                )
                )
                .then(
                        CommandManager.literal("modify")
                                .then(
                                        CommandManager.argument("name", StringArgumentType.word())
                                                .suggests(new GroupSuggestion())
                                                .then(
                                                        CommandManager.literal("selector")
                                                                .then(
                                                                        CommandManager.argument("selector", StringArgumentType.greedyString())
                                                                                .suggests((context, builder) -> EntityArgumentType.players().listSuggestions(context, builder))
                                                                                .executes((ctx) -> {
                                                                                    String groupname = StringArgumentType.getString(ctx, "name");
                                                                                    PlayerGroup group = PolCinematics.getGroupManager().resolveGroup(groupname);
                                                                                    if (group == null) {
                                                                                        ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + groupname + " not found."));
                                                                                        return 1;
                                                                                    }
                                                                                    String selectorString = StringArgumentType.getString(ctx, "selector");
                                                                                    group.setSelector(selectorString);
                                                                                    PolCinematics.getGroupManager().save();
                                                                                    ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + group.getName() + " selector set to " + selectorString + ". (Currently selects " + group.getPlayers(ctx.getSource()).size() + " online players)"));
                                                                                    return 1;
                                                                                })
                                                                )
                                                )
                                )
                );

        return groupBuilder.build();
    }



}
