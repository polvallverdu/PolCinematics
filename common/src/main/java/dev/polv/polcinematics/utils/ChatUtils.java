package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.commands.PolCinematicsCommand;

public class ChatUtils {

    public static String formatHelpMessage(String... subcommands) {
        StringBuilder message = new StringBuilder(PolCinematicsCommand.PREFIX + " §bList of subcommands:\n");

        // loop through SUBCOMMANDS array with an increment of 2
        for (int i = 0; i < subcommands.length; i += 2) {
            message.append("§3/").append(subcommands[i]).append(" §8- §b").append(subcommands[i + 1]).append("\n");
        }

        return message.toString();
    }

}
