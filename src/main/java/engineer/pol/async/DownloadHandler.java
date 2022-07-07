package engineer.pol.async;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
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
        return executorService.submit(() -> {
            try {
                BufferedImage image = ImageIO.read(new URL(url));
                return new DownloadedImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
