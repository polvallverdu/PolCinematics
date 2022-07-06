package engineer.pol.utils;


import java.awt.geom.Point2D;

public class BezierCurve {

    double x1, y1, x2, y2;

    public BezierCurve(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
        Calculates the Bezier curve point at time t.
     */
    public double getX(double t) {
        return (1 - t) * (1 - t) * x1 + 2 * (1 - t) * t * x2 + t * t * x2;
    }

    public double getY(double t) {
        return (1 - t) * (1 - t) * y1 + 2 * (1 - t) * t * y2 + t * t * y2;
    }

    public double getXY(double t) {
        return getX(t) + getY(t);
    }

    public double getPoint(double t) {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(1, 1);
        Point2D p1 = new Point2D.Double(x1, y1);
        Point2D p2 = new Point2D.Double(x2, y2);

        Point2D A = MathUtils.lerp(start, p1, t);
        Point2D B = MathUtils.lerp(p1, p2, t);
        Point2D C = MathUtils.lerp(p2, end, t);

        Point2D D = MathUtils.lerp(A, B, t);
        Point2D E = MathUtils.lerp(B, C, t);

        Point2D P = MathUtils.lerp(D, E, t);

        return P.getY();
    }

}
