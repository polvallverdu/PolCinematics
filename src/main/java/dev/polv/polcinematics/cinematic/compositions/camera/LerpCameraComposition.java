package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.utils.BasicCompositionData;

import java.util.UUID;

public class LerpCameraComposition extends CameraComposition {

    public LerpCameraComposition(String name, long duration) {
        this(UUID.randomUUID(), name, duration, new AttributeList());
    }

    private LerpCameraComposition(UUID uuid, String name, long duration, AttributeList attributeList) {
        super(uuid, name, ECameraType.LERP, duration, attributeList);

        this.declareAttribute("position", "Camera positon", EAttributeType.CAMERAPOS);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return this.getAttribute("position").getLerpCameraPos(time);
    }

    public static LerpCameraComposition fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());

        return new LerpCameraComposition(data.uuid(), data.name(), data.duration(), attributes);
    }
}
