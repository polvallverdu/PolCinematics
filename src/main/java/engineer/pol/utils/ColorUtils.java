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
