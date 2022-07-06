package engineer.pol.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import engineer.pol.async.DownloadHandler;
import engineer.pol.async.DownloadedImage;
import engineer.pol.async.Downloader;
import engineer.pol.client.PolCinematicsClient;
import engineer.pol.client.overlays.VideoOverlay;
import engineer.pol.utils.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final public class CinematicCommand {

    private static VideoOverlay videoOverlay = null;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("cinematic")
                .then(CommandManager.literal("start").executes(CinematicCommand::start))
                .then(CommandManager.literal("test").executes(CinematicCommand::test))
                .then(CommandManager.literal("stop").executes(CinematicCommand::stop)));

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (videoOverlay != null) {
                videoOverlay.render(matrixStack);
            }
        });
    }

    private static int start(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PolCinematicsClient.getInstance().getClientCinematicManager().start();
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PolCinematicsClient.getInstance().getClientCinematicManager().stop();
        videoOverlay.stop();
        return 1;
    }

    private static int test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        /*try {
            String url = "https://i.ibb.co/5xy6fVb/73930118e2a17bf8ab4175acd316f2fdc7f18f70.png";
            Future<DownloadedImage> downloader = DownloadHandler.INSTANCE.download(url);
            HudRenderCallback.EVENT.register((hud, tick) -> {
                if (!downloader.isDone()) return;

                try {
                    downloader.get().register();

                    // Get mc width and height
                    int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
                    int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
                    int windowWidth = MinecraftClient.getInstance().getWindow().getWidth();
                    int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();

                    downloader.get().rescale(windowWidth, windowHeight);

                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, downloader.get().getTextureIdentifier());

                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    DrawableHelper.drawTexture(hud, 0, 0, 0, 0, 0, width, height, windowWidth, windowHeight);
                    RenderSystem.disableBlend();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
            DrawableHelper.fill(matrixStack, 0, 0, MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), 0xFF0000FF);
        }));*/

        /*TimedTask timedTask = new TimedTask(Duration.ofSeconds(3), false);
        timedTask.startNormal();
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (timedTask.isFinished()) {
                videoOverlay.appear();
            }
        });*/
        videoOverlay.appear();

        return 1;
    }

}
