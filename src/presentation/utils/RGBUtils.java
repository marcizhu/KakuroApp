package src.presentation.utils;

import java.awt.*;

public class RGBUtils {
    // Misc settings
    private static final float luminanceThreshold = 150.0f;

    public static float luminance(Color color) {
        // Digital ITU BT.601
        // From https://www.itu.int/rec/R-REC-BT.601
        return 0.299f * color.getRed() + 0.587f * color.getGreen() + 0.114f * color.getBlue();
    }

    public static boolean isTooBright(Color color) {
        return luminance(color) > luminanceThreshold;
    }

    public static Color getContrastColor(Color color) {
        return isTooBright(color) ? Color.BLACK : Color.WHITE;
    }

    public static Color Hash2Color(Object obj) {
        return new Color(obj.hashCode());
    }
}
