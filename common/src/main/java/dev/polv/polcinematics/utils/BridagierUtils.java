package dev.polv.polcinematics.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;

public class BridagierUtils {

    public static LiteralArgumentBuilder<ServerCommandSource> goodRedirect(String alias, LiteralCommandNode<ServerCommandSource> child) {
        LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder.literal(alias);

        builder.requires(child.getRequirement())
                .forward(child.getRedirect(), child.getRedirectModifier(), child.isFork())
                .executes(child.getCommand());

        for (CommandNode<ServerCommandSource> c : child.getChildren()) {
            builder.then(c);
        }

        return builder;
    }

}
