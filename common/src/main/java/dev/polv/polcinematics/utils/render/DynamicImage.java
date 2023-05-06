package dev.polv.polcinematics.utils.render;

import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DynamicImage {

    public static BufferedImage BLACK_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);


    private BufferedImage image;
    private BufferedImage rescaledImage;
    private final DynamicResourceLocation resourceLocation;
    private NativeImage nativeImage;

    private boolean registered = false;
    private final SelfCleaningDynamicTexture texture;


    public DynamicImage(BufferedImage image) {
        this.image = image;
        this.rescaledImage = image;
        this.resourceLocation = new DynamicResourceLocation(PolCinematics.MOD_ID, UUID.randomUUID().toString().replaceAll("-", "") + "cin");
        this.nativeImage = getNativeImage(image);
        this.texture = new SelfCleaningDynamicTexture(nativeImage);

        sendToGPU();
    }

    public DynamicImage(CompletableFuture<BufferedImage> futureImage) {
        this.image = BLACK_IMAGE;
        this.rescaledImage = BLACK_IMAGE;
        this.resourceLocation = new DynamicResourceLocation(PolCinematics.MOD_ID, UUID.randomUUID().toString().replaceAll("-", "") + "cin");
        this.nativeImage = getNativeImage(BLACK_IMAGE);
        this.texture = new SelfCleaningDynamicTexture(nativeImage);

        sendToGPU();

        futureImage.thenAcceptAsync((image) -> {
            this.image = image;
            this.rescaledImage = image;
            sendToGPU();
        });
    }

    public void register() {
        if (this.registered) return;
        this.registered = true;
        MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(resourceLocation.toWorkingString().replace(':', '.'), this.texture);
    }

    private NativeImage getNativeImage(BufferedImage image) {
        NativeImage tempni = new NativeImage(image.getWidth(), image.getHeight(), true);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // the color, with red at smallest and alpha at biggest bits
                int color = image.getRGB(x, y);
                tempni.setColor(x, y, ColorUtils.getColor(new Color(color, true)));
            }
        }

        return tempni;
    }

    public BufferedImage rescale(int width, int height) {
        if (rescaledImage.getWidth() == width && rescaledImage.getHeight() == height) return rescaledImage;

        System.out.println("Rescaling image to " + width + "x" + height);

        Image rescaledImageNotBuffer = image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        rescaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        rescaledImage.getGraphics().drawImage(rescaledImageNotBuffer, 0, 0, null);

        return rescaledImage;
    }

    public boolean isDownloaded() {
        return image != BLACK_IMAGE;
    }

    public void setNewImage(BufferedImage image) {
        this.image = image;
        this.rescaledImage = image;
        sendToGPU();
    }

    public void sendToGPU() {
        this.nativeImage = getNativeImage(rescaledImage);
        this.texture.setImage(nativeImage);
        this.register();
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
