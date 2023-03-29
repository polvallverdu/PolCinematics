package dev.polv.polcinematics.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PolCinematics.MOD_ID)
public class PolCinematicsForge {

    public PolCinematicsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(PolCinematics.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        PolCinematics.init();
    }

    @Mod.EventBusSubscriber(modid = PolCinematics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            new PolCinematicsClient().onInitializeClient();
        }
    }

}
