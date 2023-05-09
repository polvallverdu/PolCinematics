package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;

public class PlayerCameraComposition extends CameraComposition {

    public static final String PERSPECTIVE_KEY = "perspective";

    @Override
    protected void declare() {
        this.declareConstant(PERSPECTIVE_KEY, "1: First Person, 2: Second Person, 3: Third Person", EValueType.INTEGER);
    }

    @Override
    protected void init(String name, ECompositionType type) {
        super.init(name, type);

        this.setPerspective(PlayerPerspective.FIRST_PERSON);
    }

    @Override
    public CameraFrame getCameraFrame(long time) {
        return null;
    }

    public PlayerPerspective getPerspective() {
        return PlayerPerspective.fromId(this.getConstant(PERSPECTIVE_KEY).getValueAsInteger());
    }

    public void setPerspective(PlayerPerspective perspective) {
        this.getConstant(PERSPECTIVE_KEY).setValue(perspective.getId());
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
            return FIRST_PERSON;
        }
    }

}
