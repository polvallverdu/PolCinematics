package engineer.pol.client.overlays;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.timeline.core.BasicComposition;
import engineer.pol.cinematic.timeline.core.CompositionProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;

public abstract class Overlay {

    public class OverlayProperties {
        public static CompositionProperty X = new CompositionProperty("x");
        public static CompositionProperty Y = new CompositionProperty("y");
        public static CompositionProperty WIDTH = new CompositionProperty("width");
        public static CompositionProperty HEIGHT = new CompositionProperty("height");
        public static CompositionProperty ALPHA = new CompositionProperty("alpha");
        public static CompositionProperty FULLSCREEN = new CompositionProperty("fullscreen", 0.0, 1.0);

        public static CompositionProperty[] values() {
            return new CompositionProperty[]{
                    X, Y, WIDTH, HEIGHT, ALPHA, FULLSCREEN
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

    protected final HashMap<CompositionProperty, BasicComposition> timelines;

    public Overlay() {
        this.timelines = new HashMap<>();

        // Find for missing properties in the timeline.
        for (CompositionProperty property : OverlayProperties.values()) {
            if (!timelines.containsKey(property)) {
                timelines.put(property, new BasicComposition());
            }
        }
    }

    protected void tick(MatrixStack matrixStack, long time) {
        double alpha = timelines.get(OverlayProperties.ALPHA).getValue(time);
        if (alpha <= 0)
            return;

        double fullscreen = timelines.get(OverlayProperties.FULLSCREEN).getValue(time);

        if (fullscreen >= 1) {
            this.render(matrixStack, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), alpha, time);
            return;
        }

        double x = timelines.get(OverlayProperties.X).getValue(time);
        double y = timelines.get(OverlayProperties.Y).getValue(time);
        double width = timelines.get(OverlayProperties.WIDTH).getValue(time);
        double height = timelines.get(OverlayProperties.HEIGHT).getValue(time);

        if (fullscreen > 0) {
            int widthWindow = MinecraftClient.getInstance().getWindow().getWidth();
            int heightWindow = MinecraftClient.getInstance().getWindow().getHeight();

            // calculate the difference between the size (fullscreen = 0) and the minecraft widow size (fullscreen = 1) relative to fullscreen
            x = 0 + (widthWindow - width) * fullscreen;
            y = 0 + (heightWindow - height) * fullscreen;
            width = widthWindow + (widthWindow - width) * fullscreen;
            height = heightWindow + (heightWindow - height) * fullscreen;
        }

        this.render(matrixStack, (int) x, (int) y, (int) width, (int) height, alpha, time);
    }

    public abstract void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time);

    protected void addTimelineProperty(CompositionProperty property) {
        // Check if property is already in the timeline comparing the name.
        // If true, change the key to the new property.
        // If false, add the property to the timeline.
        for (CompositionProperty compositionProperty : timelines.keySet()) {
            if (compositionProperty.getName().equals(property.getName())) {
                timelines.put(property, timelines.get(compositionProperty));
                timelines.remove(compositionProperty);
                return;
            }
        }
        timelines.put(property, new BasicComposition());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (CompositionProperty property : OverlayProperties.values()) {
            json.add(property.getName(), timelines.get(property).toJson());
        }
        return json;
    }

    public void fromJson(JsonObject json) {
        // Get timeline properties from json if not in OverlayProperties.
        for (String key : json.keySet()) {
            CompositionProperty property = OverlayProperties.valueOf(key);
            if (property == null) {
                property = new CompositionProperty(key);
            }
            timelines.put(property, BasicComposition.fromJson(json.get(key).getAsJsonObject()));
        }
    }

}
