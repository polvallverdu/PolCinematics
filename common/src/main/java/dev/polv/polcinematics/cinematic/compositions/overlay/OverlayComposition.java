package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public abstract class OverlayComposition extends Composition {

    private final EOverlayType overlayType;

    public OverlayComposition(String name, EOverlayType overlayType, long duration) {
        this(UUID.randomUUID(), name, overlayType, duration, new AttributeList());
    }

    public OverlayComposition(UUID uuid, String name, EOverlayType overlayType, long duration, AttributeList attributes) {
        super(uuid, name, duration, ECompositionType.OVERLAY_COMPOSITION, attributes);
        this.overlayType = overlayType;
    }

    public abstract void tick(MatrixStack MatrixStack, long time);

    //public abstract void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time);

    public JsonObject toJson() {
        JsonObject json = super.toJson();

        json.addProperty("overlayType", overlayType.getName());

        return json;
    }

    public static OverlayComposition fromJson(JsonObject json) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        /*UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        EOverlayType overlayType = EOverlayType.fromName(json.get("overlayType").getAsString());
        long duration = json.get("duration").getAsLong();

        switch (overlayType) { // TODO
            case SOLID_COLOR_OVERLAY -> {
                return null;
            }
            default -> {
                return null;
            }
        }*/
        EOverlayType overlayType = EOverlayType.fromName(json.get("overlayType").getAsString());
        var compositionClass =  overlayType.getClazz();
        Method m = compositionClass.getMethod("fromJson", JsonObject.class);
        var res = m.invoke(null, json);
        return (OverlayComposition) res;
    }

}