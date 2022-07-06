package engineer.pol;

import engineer.pol.commands.ModCommands;
import net.fabricmc.api.ModInitializer;

public class PolCinematics implements ModInitializer {

    public static String MODID = "polcinematics";

    @Override
    public void onInitialize() {
        ModCommands.registerCommands();
    }
}
