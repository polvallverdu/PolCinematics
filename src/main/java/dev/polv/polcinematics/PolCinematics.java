package dev.polv.polcinematics;

import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.ModCommands;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefMessageRouter;

import java.io.File;
import java.io.IOException;

public class PolCinematics implements ModInitializer {

    public static String MODID = "polcinematics";
    public static ServerCinematicManager CINEMATICS_MANAGER;
    public static MinecraftServer SERVER = null;

    @Override
    public void onInitialize() {
        ModCommands.registerCommands();
        CINEMATICS_MANAGER = new ServerCinematicManager();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER = null;
        });
    }

    public static void main(String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        //Create a new CefAppBuilder instance
        CefAppBuilder builder = new CefAppBuilder();

//Configure the builder instance
        builder.setInstallDir(new File("jcef-bundle")); //Default
        builder.setProgressHandler(new ConsoleProgressHandler()); //Default
        builder.addJcefArgs("--disable-gpu"); //Just an example
        builder.getCefSettings().windowless_rendering_enabled = false; //Default - select OSR mode

//Set an app handler. Do not use CefApp.addAppHandler(...), it will break your code on MacOSX!
        builder.setAppHandler(new MavenCefAppHandlerAdapter(){
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                super.stateHasChanged(state);
            }
        });

//Build a CefApp instance using the configuration above
        CefApp app = builder.build();
        CefClient client = app.createClient();

        CefMessageRouter msgRouter = CefMessageRouter.create();
        client.addMessageRouter(msgRouter);

        var browser = client.createBrowser("https://www.google.com", false, true);
    }

}
