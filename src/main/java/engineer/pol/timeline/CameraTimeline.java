package engineer.pol.timeline;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class CameraTimeline {

    private UUID uuid;
    private String name;

    public enum CameraProperty {
        X, Y, Z, XYZ_PLAYER_LOCKED,
        PITCH, YAW, ROLL,
        LOCKX, LOCKY, LOCKZ, LOCK_ROTATION, PLAYER_ROTATION,
        FOV;
    }

    private final HashMap<CameraProperty, Timeline<Double>> timelines;

    public CameraTimeline() {
        this.uuid = UUID.randomUUID();
        this.name = "";
        timelines = new HashMap<>();

        for (CameraProperty property : CameraProperty.values()) {
            timelines.put(property, new Timeline<>());
        }
    }

    public void addKeyframe(CameraProperty property, long time, double value) {
        timelines.get(property).addKeyframe(time, value);
    }

    public void addKeyframe(CameraProperty property, long time, double value, Easing easing) {
        timelines.get(property).addKeyframe(time, value, easing);
    }

    public void removeKeyframe(CameraProperty property, long time) {
        timelines.get(property).removeKeyframe(time);
    }

    public Timeline<Double> getTimeline(CameraProperty property) {
        return timelines.get(property);
    }

    public double getValue(CameraProperty property, long time) {
        return timelines.get(property).getValue(time);
    }

    public double getValueWithEasing(CameraProperty property, long time) {
        return timelines.get(property).getEasedValue(time);
    }

    @Environment(EnvType.CLIENT)
    public CameraPos getCameraPos(long time) {
        double playerXYZLocked = this.getValueWithEasing(CameraProperty.XYZ_PLAYER_LOCKED, time);
        double lockedRotation = this.getValueWithEasing(CameraProperty.LOCK_ROTATION, time);

        Vec3d headPos = MinecraftClient.getInstance().player.getEyePos();
        double playerX = headPos.getX();
        double playerY = headPos.getY();
        double playerZ = headPos.getZ();
        double playerPitch = MinecraftClient.getInstance().player.getPitch();
        double playerYaw = MinecraftClient.getInstance().player.getHeadYaw();

        double x = playerX;
        double y = playerY;
        double z = playerZ;

        if (playerXYZLocked < 1) {
            x = this.getValueWithEasing(CameraProperty.X, time);
            y = this.getValueWithEasing(CameraProperty.Y, time);
            z = this.getValueWithEasing(CameraProperty.Z, time);
            if (playerXYZLocked > 0) {
                // Calculate relative position to player where 0 is the camera and 1 is the player.
                // Multiply the difference with playerXYZLocked to get the relative position.
                x = playerX + (x - playerX) * playerXYZLocked;
                y = playerY + (y - playerY) * playerXYZLocked;
                z = playerZ + (z - playerZ) * playerXYZLocked;
            }
        }

        boolean playerRotation = this.getValue(CameraProperty.PLAYER_ROTATION, time) == 1;

        double pitch = playerRotation ? playerPitch : this.getValueWithEasing(CameraProperty.PITCH, time);
        double yaw = playerRotation ? playerYaw : this.getValueWithEasing(CameraProperty.YAW, time);
        double roll = playerRotation ? 0 : this.getValueWithEasing(CameraProperty.ROLL, time);

        if (lockedRotation > 0) {
            double lockedX = this.getValueWithEasing(CameraProperty.LOCKX, time);
            double lockedY = this.getValueWithEasing(CameraProperty.LOCKY, time);
            double lockedZ = this.getValueWithEasing(CameraProperty.LOCKZ, time);
            Vec3d lockedPosition = new Vec3d(lockedX, lockedY, lockedZ);
            Vec3d cameraPosition = new Vec3d(x, y, z);
            Vec3d relativePosition = lockedPosition.subtract(cameraPosition);

            // Get pitch, yaw, and roll from relative position.
            double lockedPitch = Math.asin(relativePosition.y / relativePosition.length());
            double lockedYaw = Math.atan2(relativePosition.z, relativePosition.x);
            double lockedRoll = Math.atan2(relativePosition.y, Math.sqrt(relativePosition.x * relativePosition.x + relativePosition.z * relativePosition.z));

            if (lockedRotation < 1) {
                pitch = lockedPitch + (pitch - lockedPitch) * lockedRotation;
                yaw = lockedYaw + (yaw - lockedYaw) * lockedRotation;
                roll = lockedRoll + (roll - lockedRoll) * lockedRotation;
            } else {
                pitch = lockedPitch;
                yaw = lockedYaw;
                roll = lockedRoll;
            }
        }

        return new CameraPos(x, y, z, pitch, yaw, roll);
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
