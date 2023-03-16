package engineer.pol.utils.math;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathUtils {

    /**
     * Linear interpolation between two values
     *
     * @param a Value at t = 0
     * @param b Value at t = 1
     * @param t Interpolation value
     * @return Interpolated value
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    /**
     * Calculates where two vectors intersect in a 3D space.
     *
     * @param pos1 Position of the first vector
     * @param rot1 Rotation of the first vector
     * @param pos2 Position of the second vector
     * @param rot2 Rotation of the second vector
     * @return The point where the two vectors intersect
     */
    public static Vec3d calculateCuttingPoint(Vec3d pos1, Vec2f rot1, Vec3d pos2, Vec2f rot2) {
        double x1 = pos1.getX();
        double y1 = pos1.getY();
        double z1 = pos1.getZ();
        double pitch1 = Math.toRadians(rot1.x);
        double yaw1 = Math.toRadians(rot1.y);

        double x2 = pos2.getX();
        double y2 = pos2.getY();
        double z2 = pos2.getZ();
        double pitch2 = Math.toRadians(rot2.x);
        double yaw2 = Math.toRadians(rot2.y);

        double a1 = Math.sin(pitch1);
        double b1 = Math.cos(pitch1) * Math.sin(yaw1);
        double c1 = Math.cos(pitch1) * Math.cos(yaw1);

        double a2 = Math.sin(pitch2);
        double b2 = Math.cos(pitch2) * Math.sin(yaw2);
        double c2 = Math.cos(pitch2) * Math.cos(yaw2);

        double d = (a1 * b2) - (a2 * b1);
        double e = (a1 * c2) - (a2 * c1);
        double f = (b1 * c2) - (b2 * c1);

        double g = Math.sqrt((d * d) + (e * e) + (f * f));

        double x = ((y1 * f) - (z1 * e) - (y2 * f) + (z2 * e)) / g + x1;
        double y = ((z1 * d) - (x1 * f) - (z2 * d) + (x2 * f)) / g + y1;
        double z = ((x1 * e) - (y1 * d) - (x2 * e) + (y2 * d)) / g + z1;

        return new Vec3d(x, y, z);
    }

    /**
     * Linear spherical interpolation between two points in a 3D space, and a center point.
     *
     * @param pos1 Position when t = 0
     * @param pos2 Position when t = 1
     * @param center Center point
     * @param t Interpolation value
     * @return Interpolated position
     */
    public static Vec3d slerp(/*double x1, double y1, double z1*/Vec3d pos1, /*double x2, double y2, double z2*/Vec3d pos2, /*double cx, double cy, double cz*/Vec3d center, double t) {
        // Calculate the vectors from the center point to each endpoint
        double[] v1 = new double[] { pos1.getX() - center.getX(), pos1.getY() - center.getY(), pos1.getZ() - center.getZ() };
        double[] v2 = new double[] { pos2.getX() - center.getX(), pos2.getY() - center.getY(), pos2.getZ() - center.getZ() };

        // Normalize the vectors
        double v1Len = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);
        double v2Len = Math.sqrt(v2[0] * v2[0] + v2[1] * v2[1] + v2[2] * v2[2]);
        v1[0] /= v1Len;
        v1[1] /= v1Len;
        v1[2] /= v1Len;
        v2[0] /= v2Len;
        v2[1] /= v2Len;
        v2[2] /= v2Len;

        // Calculate the dot product of the two vectors
        double dot = v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];

        // Calculate the angle between the two vectors
        double angle = Math.acos(dot);

        // Calculate the sin of the angle
        double sinAngle = Math.sin(angle);

        // Calculate the weights for each endpoint
        double weight1 = Math.sin((1 - t) * angle) / sinAngle;
        double weight2 = Math.sin(t * angle) / sinAngle;

        // Calculate the slerped point
        Vec3d slerp = new Vec3d(
                weight1 * v1[0] + weight2 * v2[0],
                weight1 * v1[1] + weight2 * v2[1],
                weight1 * v1[2] + weight2 * v2[2]
        );

        return slerp;
    }

}
