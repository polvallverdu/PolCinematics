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

public class DownloadedImage {

    private final BufferedImage image;
    private BufferedImage rescaledImage;
    private final DynamicResourceLocation resourceLocation;
    private NativeImage nativeImage = new NativeImage(1, 1, true);

    private boolean registered = false;
    private final SelfCleaningDynamicTexture texture;

    public DownloadedImage(BufferedImage image) {
        this.image = image;
        this.rescaledImage = image;
        this.resourceLocation = new DynamicResourceLocation(PolCinematics.MODID, image.hashCode() + "cin");
        this.nativeImage = new NativeImage(1, 1, true);
        this.texture = new SelfCleaningDynamicTexture(nativeImage);
    }

    public void register() {
        if (this.registered) return;
        this.registered = true;
        MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(resourceLocation.toWorkingString().replace(':', '.'), this.texture);
    }

    public BufferedImage getImage() {
        return rescaledImage;
    }

    public BufferedImage rescale(int width, int height) {
        if (rescaledImage.getWidth() == width && rescaledImage.getHeight() == height) return rescaledImage;

        System.out.println("Rescaling image to " + width + "x" + height);

        Image rescaledImageNotBuffer = image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        rescaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        rescaledImage.getGraphics().drawImage(rescaledImageNotBuffer, 0, 0, null);

        NativeImage tempni = new NativeImage(rescaledImage.getWidth(), rescaledImage.getHeight(), true);
        for (int x = 0; x < rescaledImage.getWidth(); x++) {
            for (int y = 0; y < rescaledImage.getHeight(); y++) {
                // the color, with red at smallest and alpha at biggest bits
                int color = rescaledImage.getRGB(x, y);
                tempni.setColor(x, y, ColorUtils.getColor(new Color(color, true)));
            }
        }

        this.nativeImage = tempni;
        this.texture.setImage(tempni);
        this.register();

        return rescaledImage;
    }

    public Identifier getTextureIdentifier() {
        return resourceLocation;
    }

    public int getWidth() {
        return rescaledImage.getWidth();
    }

    public int getHeight() {
        return rescaledImage.getHeight();
    }

    public int getRealWidth() {
        return image.getWidth();
    }

    public int getRealHeight() {
        return image.getHeight();
    }

    public NativeImage getNativeImage() {
        return nativeImage;
    }

}
