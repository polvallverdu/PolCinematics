package engineer.pol.async;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

public class Downloader implements Callable<DownloadedImage> {

    private String imageURL;

    protected Downloader(String URL) {
        this.imageURL = URL;
    }

    @Override
    public DownloadedImage call() throws Exception {
        BufferedImage image = ImageIO.read(new URL(imageURL));
        return new DownloadedImage(image);
    }
}
