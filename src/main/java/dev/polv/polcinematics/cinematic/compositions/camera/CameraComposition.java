package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.cinematic.compositions.core.*;

import java.util.UUID;

public abstract class CameraComposition extends Composition {

    private final ECameraType cameraType;

    public CameraComposition(String name, ECameraType cameraType, long duration) {
        super(UUID.randomUUID(), name, duration, ECompositionType.CAMERA_COMPOSITION);
        this.cameraType = cameraType;
    }

    protected CameraComposition(UUID uuid, String name, ECameraType cameraType, long duration, AttributeList attributes) {
        super(uuid, name, duration, ECompositionType.CAMERA_COMPOSITION, attributes);
        this.cameraType = cameraType;
    }

/*
    public void addKeyframe(CompositionProperty property, long time, double value) {
        timelines.get(property).addKeyframe(time, value);
    }

    public void addKeyframe(CompositionProperty property, long time, double value, Easing easing) {
        timelines.get(property).addKeyframe(time, value, easing);
    }

    public void removeKeyframe(CompositionProperty property, long time) {
        timelines.get(property).removeKeyframe(time);
    }

    public Attribute getTimeline(CompositionProperty property) {
        return timelines.get(property);
    }

    public double getValue(CompositionProperty property, long time) {
        return timelines.get(property).getValue(time);
    }

    public double getValueWithEasing(CompositionProperty property, long time) {
        return timelines.get(property).getValue(time);
    }

    public void smooth() {
        timelines.forEach((property, composition) -> {
            composition.orderEasings(Easing.EASE_IN_CUBIC, Easing.LINEAR, Easing.EASE_OUT_CUBIC);
        });
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
    }*/

    public abstract CameraPos getCameraPos(long time);

    public JsonObject toJson() {
        JsonObject json = super.toJson();

        json.addProperty("cameraType", cameraType.getName());

        return json;
    }

    public static CameraComposition fromJson(JsonObject json) {
        /*UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        ECameraType cameraType = ECameraType.fromName(json.get("cameraType").getAsString());
        long duration = json.get("duration").getAsLong();

        switch (cameraType) {
            case FIXED -> {
                return null;
            }
            default -> {
                return null;
            }
        }*/
        ECameraType cameraType = ECameraType.fromName(json.get("cameraType").getAsString());
        var compositionClass = cameraType.getClazz();
        CameraComposition composition = compositionClass.cast(Composition.fromJson(json));
        return composition;
    }

}
