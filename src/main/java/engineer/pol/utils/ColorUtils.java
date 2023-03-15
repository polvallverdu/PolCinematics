package engineer.pol.utils;

import java.awt.*;

public class ColorUtils {

    // Color format: 0xAARRGGBB

    /**
     * Returns the int color for Minecraft
     * @param red 0-255
     * @param green 0-255
     * @param blue 0-255
     * @return int color
     */
    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int[] colors = {alpha, red, green, blue};
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] > 255) colors[i] = 255;
            if (colors[i] < 0) colors[i] = 0;
        }
        return (colors[0] << 24) | (colors[1] << 16) | (colors[2] << 8) | colors[3];
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    // Color format: 0xAARRGGBB
    // alpha goes from 0 to 255
    public static int applyAlphaToColor(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    // Color format: 0xAARRGGBB
    // alpha is 0.0-1.0
    public static int applyAlphaToColor(int color, double alpha) {
        return (int) (alpha * 255) << 24 | (color & 0x00FFFFFF);
    }
}
