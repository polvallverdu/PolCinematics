package dev.polv.polcinematics.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.commands.AudioCommand;
import dev.polv.polcinematics.commands.CinematicCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PolCinematics.MOD_ID)
public class PolCinematicsForge {

    public PolCinematicsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(PolCinematics.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        PolCinematics.init();
    }

}
