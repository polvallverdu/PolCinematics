package engineer.pol.utils;

import java.awt.*;

public class ColorUtils {

    // Color format: 0xAARRGGBB

    public static int getColor(int red, int green, int blue) {
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    // Color format: 0xAARRGGBB
    public static int getColorFromBufferedImage(int color) {
        return (color & 0xFF000000) >> 24;
    }
}
