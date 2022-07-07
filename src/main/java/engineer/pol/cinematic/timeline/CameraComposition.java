package engineer.pol.cinematic.timeline;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.timeline.core.*;
import engineer.pol.utils.math.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class CameraComposition extends Composition {

    public static class CameraProperty {
        public static CompositionProperty X = new CompositionProperty("x");
        public static CompositionProperty Y = new CompositionProperty("y");
        public static CompositionProperty Z = new CompositionProperty("z");
        public static CompositionProperty XYZ_PLAYER_LOCKED = new CompositionProperty("xyz_player_locked", 0.0, 1.0);


        public static CompositionProperty PITCH = new CompositionProperty("pitch");
        public static CompositionProperty YAW = new CompositionProperty("yaw");
        public static CompositionProperty ROLL = new CompositionProperty("roll");


        public static CompositionProperty LOCKX = new CompositionProperty("lockx");
        public static CompositionProperty LOCKY = new CompositionProperty("locky");
        public static CompositionProperty LOCKZ = new CompositionProperty("lockz");
        public static CompositionProperty LOCK_ROTATION = new CompositionProperty("lock_rotation", 0.0, 1.0);
        public static CompositionProperty PLAYER_ROTATION = new CompositionProperty("player_rotation", 0.0, 1.0);

        public static CompositionProperty[] values() {
            return new CompositionProperty[]{
                    X, Y, Z, XYZ_PLAYER_LOCKED,
                    PITCH, YAW, ROLL,
                    LOCKX, LOCKY, LOCKZ, LOCK_ROTATION, PLAYER_ROTATION
            };
        }

        public static CompositionProperty valueOf(String name) {
            for (CompositionProperty property : values()) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
            return null;
        }
    }

    private final HashMap<CompositionProperty, BasicComposition> timelines;

    public CameraComposition(String name, long duration) {
        this(UUID.randomUUID(), name, new HashMap<>(), duration);
    }

    public CameraComposition(UUID uuid, String name, HashMap<CompositionProperty, BasicComposition> timelines, long duration) {
        super(uuid, name, duration, CompositionType.CAMERA_COMPOSITION);
        this.timelines = timelines;

        // Find for missing properties in the timeline.
        for (CompositionProperty property : CameraProperty.values()) {
            if (!timelines.containsKey(property)) {
                timelines.put(property, new BasicComposition());
            }
        }

        this.timelines.forEach((property, composition) -> {
            composition.sort();
        });
    }

    public void addKeyframe(CompositionProperty property, long time, double value) {
        timelines.get(property).addKeyframe(time, value);
    }

    public void addKeyframe(CompositionProperty property, long time, double value, Easing easing) {
        timelines.get(property).addKeyframe(time, value, easing);
    }

    public void removeKeyframe(CompositionProperty property, long time) {
        timelines.get(property).removeKeyframe(time);
    }

    public BasicComposition getTimeline(CompositionProperty property) {
        return timelines.get(property);
    }

    public double getValue(CompositionProperty property, long time) {
        return timelines.get(property).getValue(time);
    }

    public double getValueWithEasing(CompositionProperty property, long time) {
        return timelines.get(property).getValue(time);
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

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("duration", this.getDuration());
        json.addProperty("type", this.getType().getId());

        this.timelines.forEach((property, timeline) -> {
            json.add(property.getName(), timeline.toJson());
        });

        return json;
    }

    public static CameraComposition fromJson(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        long duration = json.get("duration").getAsLong();

        HashMap<CompositionProperty, BasicComposition> timelines = new HashMap<>();
        json.entrySet().forEach((entry) -> {
            if (entry.getKey().equals("uuid") || entry.getKey().equals("name")) {
                return;
            }
            timelines.put(CameraProperty.valueOf(entry.getKey()), BasicComposition.fromJson(entry.getValue().getAsJsonObject()));
        });

        return new CameraComposition(uuid, name, timelines, duration);
    }

}
