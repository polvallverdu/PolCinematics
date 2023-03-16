package engineer.pol.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.utils.BasicCompositionData;

import java.util.UUID;

public class PlayerCameraComposition extends CameraComposition {


    public PlayerCameraComposition(String name, ECameraType cameraType, long duration) {
        super(name, cameraType, duration);
    }

    private PlayerCameraComposition(UUID uuid, String name, ECameraType cameraType, long duration, AttributeList attributeList) {
        super(uuid, name, cameraType, duration, attributeList);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return null;
    }

    public static PlayerCameraComposition fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json);

        return new PlayerCameraComposition(data.uuid(), data.name(), ECameraType.FIXED, data.duration(), attributes);
    }
}
