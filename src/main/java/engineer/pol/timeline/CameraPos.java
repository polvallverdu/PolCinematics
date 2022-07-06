package engineer.pol.timeline;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.util.math.Vec3d;

public class CameraPos {

    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;
    private final double roll;

    public CameraPos(double x, double y, double z, double pitch, double yaw, double roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public double getRoll() {
        return roll;
    }

    public static CameraPos fromConfig(Config config) {
        double x = config.get("x");
        double y = config.get("y");
        double z = config.get("z");
        double pitch = config.get("pitch");
        double yaw = config.get("yaw");
        double roll = config.get("roll");
        return new CameraPos(x, y, z, pitch, yaw, roll);
    }

    public Config toConfig() {
        Config config = Config.inMemory();
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("pitch", pitch);
        config.set("yaw", yaw);
        config.set("roll", roll);
        return config;
    }

}
