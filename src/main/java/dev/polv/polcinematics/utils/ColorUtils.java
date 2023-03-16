package dev.polv.polcinematics.utils;

import java.awt.*;

public class ColorUtils {

    // Color format: 0xAARRGGBB

    /**
     * Returns the int color for Minecraft
     *
     * @param red 0-255
     * @param green 0-255
     * @param blue 0-255
     * @return The color in the format 0xAARRGGBB. Alpha is set to 255 (no transparency)
     */
    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    /**
     * Returns the int color for Minecraft
     *
     * @param red 0-255
     * @param green 0-255
     * @param blue 0-255
     * @param alpha 0-255
     * @returnThe color in the format 0xAARRGGBB.
     */
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

    /**
     * Applies an alpha value to a color.
     *
     * @param color The color to apply the alpha to. This is an int in the format 0xAARRGGBB
     * @param alpha The alpha value to apply. This is an int between 0 and 255
     * @return The color with the alpha applied
     */
    public static int applyAlphaToColor(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Applies an alpha value to a color.
     *
     * @param color The color to apply the alpha to. This is an int in the format 0xAARRGGBB
     * @param alpha The alpha value to apply. This is a double between 0.0 and 1.0
     * @return The color with the alpha applied
     */
    public static int applyAlphaToColor(int color, double alpha) {
        return (int) (alpha * 255) << 24 | (color & 0x00FFFFFF);
    }

    /**
     * Splits the red, green, blue and alpha values of a color into an array of ints. These will go from 0 to 255.
     *
     * @param color The color to split
     * @return An array of ints with the red, green, blue and alpha values
     */
    public static int[] splitColors(Color color) {
        return new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
    }
}
