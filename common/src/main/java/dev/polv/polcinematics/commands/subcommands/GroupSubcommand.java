package dev.polv.polcinematics.commands.subcommands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.groups.PlayerGroup;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.utils.CommandUtils;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GroupSubcommand {

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralArgumentBuilder<ServerCommandSource> groupBuilder = CommandUtils.l("groups");

        groupBuilder
                .then(
                        CommandUtils.l("create")
                                .then(
                                        CommandManager.argument("name", StringArgumentType.word())
                                                .then(
                                                        CommandUtils.arg_selector()
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
                        CommandUtils.l("list")
                                .executes((ctx) -> {
                                    ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Groups:"));
                                    PolCinematics.getGroupManager().getGroups().forEach((group) -> {
                                        ctx.getSource().sendMessage(Text.of(group.getName() + " (" + group.getPlayers(ctx.getSource()).size() + " players)"));
                                    });
                                    return 1;
                                })
                )
                .then(
                        CommandUtils.l("delete")
                                .then(
                                        CommandUtils.arg_group()
                                                .executes((ctx) -> {
                                                    PlayerGroup group = CommandUtils.getGroup(ctx);

                                                    PolCinematics.getGroupManager().deleteGroup(group);
                                                    ctx.getSource().sendMessage(Text.of(PolCinematicsCommand.PREFIX + "Group " + group.getName() + " deleted."));
                                                    return 1;
                                                })
                                )
                )
                .then(
                        CommandUtils.l("modify")
                                .then(
                                        CommandUtils.arg_group()
                                                .then(
                                                        CommandUtils.l("selector")
                                                                .then(
                                                                        CommandUtils.arg_selector()
                                                                        .executes((ctx) -> {
                                                                                    PlayerGroup group = CommandUtils.getGroup(ctx);

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
