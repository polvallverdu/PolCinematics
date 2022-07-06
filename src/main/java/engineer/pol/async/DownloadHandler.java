package engineer.pol.async;

import engineer.pol.PolCinematics;
import engineer.pol.utils.ColorUtils;
import engineer.pol.utils.DynamicResourceLocation;
import engineer.pol.utils.SelfCleaningDynamicTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadHandler {

    public static DownloadHandler INSTANCE = new DownloadHandler();

    private final ExecutorService executorService;

    protected DownloadHandler() {
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public Future<DownloadedImage> download(String url) {
        Downloader downloader = new Downloader(url);
        return executorService.submit(downloader);
    }

}
