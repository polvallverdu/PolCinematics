package dev.polv.polcinematics.utils.math;

import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

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

    public static Vector3d slerp(CameraFrame pre, CameraFrame post, double t) {
        /*Vector3d p0 = pre.getVector3d();
        Vector3d p1 = post.getVector3d();

        // Define the start and end rotations as quaternions
        Quaterniond startRotation = pre.getQuaterniond();
        Quaterniond endRotation = post.getQuaterniond();

        // Perform slerp between the two quaternions based on the value of t
        Quaterniond slerpRotation = new Quaterniond();
        slerpRotation.slerp(startRotation, t, endRotation);

        // Interpolate between the two points based on the slerp rotation
        Vector3d interpolatedPoint = p0.mul(1.0f - t, new Vector3d()).add(p1.mul(t, new Vector3d()));
        interpolatedPoint.rotate(slerpRotation);*/

        return pre.getVector3d(); // TODO: TEMP
    }

}
