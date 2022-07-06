package engineer.pol.utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class MathUtils {

    public static Point2D lerp(Point2D a, Point2D b, double t) {
        return new Point2D.Double(a.getX() + (b.getX() - a.getX()) * t, a.getY() + (b.getY() - a.getY()) * t);
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

}
