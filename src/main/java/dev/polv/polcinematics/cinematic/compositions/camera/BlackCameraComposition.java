package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.utils.BasicCompositionData;

import java.util.UUID;

public class BlackCameraComposition extends CameraComposition {


    public BlackCameraComposition(String name, long duration) {
        super(name, ECameraType.BLACK, duration);
    }

    private BlackCameraComposition(UUID uuid, String name, long duration, AttributeList attributeList) {
        super(uuid, name, ECameraType.BLACK, duration, attributeList);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return null;
    }

    public static BlackCameraComposition fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json);

        return new BlackCameraComposition(data.uuid(), data.name(), data.duration(), attributes);
    }
}
