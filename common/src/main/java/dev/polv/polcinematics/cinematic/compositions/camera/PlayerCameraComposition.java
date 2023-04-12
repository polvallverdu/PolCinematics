package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.utils.BasicCompositionData;

import java.util.UUID;

public class PlayerCameraComposition extends CameraComposition {

    public static final String PERSPECTIVE_KEY = "perspective";

    @Override
    protected void declareVariables() {
        this.declareProperty(PERSPECTIVE_KEY, "1: First Person, 2: Second Person, 3: Third Person", EValueType.INTEGER);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return null;
    }

    @Override
    public CameraRot getCameraRot(long time) {
        return null;
    }

    public PlayerPerspective getPerspective() {
        return PlayerPerspective.fromId(this.getProperty(PERSPECTIVE_KEY).getValueAsInteger());
    }

    public enum PlayerPerspective {
        FIRST_PERSON(1),
        SECOND_PERSON(2),
        THIRD_PERSON(3);

        private int id;

        PlayerPerspective(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static PlayerPerspective fromId(int id) {
            for (PlayerPerspective perspective : values()) {
                if (perspective.getId() == id) {
                    return perspective;
                }
            }
            return null;
        }
    }

}
