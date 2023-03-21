package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.utils.BasicCompositionData;

import java.util.UUID;

public class SlerpCameraComposition extends CameraComposition {

    public SlerpCameraComposition(String name, long duration) {
        this(UUID.randomUUID(), name, duration, new AttributeList());
    }

    private SlerpCameraComposition(UUID uuid, String name, long duration, AttributeList attributeList) {
        super(uuid, name, ECameraType.SLERP, duration, attributeList);

        this.declareAttribute("position", "Camera positon", EAttributeType.CAMERAPOS);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return this.getAttribute("position").getSlerpCameraPos(time);
    }

    public static SlerpCameraComposition fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());

        return new SlerpCameraComposition(data.uuid(), data.name(), data.duration(), attributes);
    }
}
