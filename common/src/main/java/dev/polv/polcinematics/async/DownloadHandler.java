package dev.polv.polcinematics.async;

import dev.polv.polcinematics.utils.render.DynamicImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadHandler {

    public static DownloadHandler INSTANCE = new DownloadHandler();

    private final ExecutorService executorService;

    protected DownloadHandler() {
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public DynamicImage downloadImage(String url) {
        CompletableFuture<BufferedImage> futureImage = CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new URL(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);
        return new DynamicImage(futureImage);
    }

}
