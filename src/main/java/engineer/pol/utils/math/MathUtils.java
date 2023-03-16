package engineer.pol.utils.math;

public class MathUtils {

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static double[] calculateCuttingPoint(double x1, double y1, double z1, double angle1, double x2, double y2, double z2, double angle2) {
        double[] vector1 = new double[]{Math.cos(angle1), Math.sin(angle1), 0};
        double[] vector2 = new double[]{Math.cos(angle2), Math.sin(angle2), 0};

        double[] crossProduct = new double[]{
                vector1[1] * vector2[2] - vector1[2] * vector2[1],
                vector1[2] * vector2[0] - vector1[0] * vector2[2],
                vector1[0] * vector2[1] - vector1[1] * vector2[0]
        };

        double dotProduct1 = x1 * crossProduct[0] + y1 * crossProduct[1] + z1 * crossProduct[2];
        double dotProduct2 = x2 * crossProduct[0] + y2 * crossProduct[1] + z2 * crossProduct[2];
        double scalar1 = dotProduct1 / (Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2) + Math.pow(vector1[2], 2));
        double scalar2 = dotProduct2 / (Math.pow(vector2[0], 2) + Math.pow(vector2[1], 2) + Math.pow(vector2[2], 2));

        return new double[]{
                x1 + scalar1 * vector1[0],
                y1 + scalar1 * vector1[1],
                z1 + scalar1 * vector1[2]
        };
    }

    public static double[] slerp(double x1, double y1, double z1, double x2, double y2, double z2, double cx, double cy, double cz, double t) {
        // Calculate the vectors from the center point to each endpoint
        double[] v1 = new double[] { x1 - cx, y1 - cy, z1 - cz };
        double[] v2 = new double[] { x2 - cx, y2 - cy, z2 - cz };

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
        double[] slerp = new double[] {
                weight1 * x1 + weight2 * x2,
                weight1 * y1 + weight2 * y2,
                weight1 * z1 + weight2 * z2
        };

        return slerp;
    }



}
