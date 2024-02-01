package dev.polv.polcinematics.cinematic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.internal.compositions.Composition;
import dev.polv.polcinematics.internal.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.internal.compositions.types.camera.ECameraType;
import dev.polv.polcinematics.internal.compositions.types.overlay.OverlayComposition;
import dev.polv.polcinematics.internal.layers.CameraLayer;
import dev.polv.polcinematics.internal.layers.Layer;
import dev.polv.polcinematics.internal.layers.WrappedComposition;
import dev.polv.polcinematics.exception.OverlapException;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinematic {

    private final UUID uuid;
    private String name;
    private long duration;

    private final CameraLayer cameraLayer;
    private final List<Layer> layers;

    protected Cinematic(UUID uuid, String name, long duration, CameraLayer cameraLayer, List<Layer> layers) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.cameraLayer = cameraLayer;
        this.layers = layers;
    }

    /**
     * Adds a new layer to the cinematic
     *
     * @return The created {@link Layer}
     */
    public Layer addLayer() {
        Layer layer = new Layer();
        this.layers.add(layer);
        return layer;
    }

    /**
     * Removes a layer from the cinematic by its index
     *
     * @param index The index of the layer to remove
     */
    public void removeLayer(int index) {
        this.layers.remove(index);
    }

    /**
     * Removes a layer from the cinematic
     *
     * @param layer The layer to remove
     * @return {@code true} if the layer was removed, {@code false} otherwise
     */
    public boolean removeLayer(Layer layer) {
        return this.layers.remove(layer);
    }

    public boolean canMove(Layer layer, int positions, boolean isUp) {
        int index = this.layers.indexOf(layer);
        if (isUp) {
            index -= positions;
        } else {
            index += positions;
        }
        return index >= 0 && index < this.layers.size();
    }

    public void moveLayer(Layer layer, int positions, boolean isUp) {
        int index = this.layers.indexOf(layer);
        if (isUp) {
            index -= positions;
        } else {
            index += positions;
        }
        this.layers.remove(layer);
        this.layers.add(index, layer);
    }

    /**
     * Moves a composition from one layer to another, and changes its start time
     *
     * @param composition The {@link WrappedComposition} to move
     * @param oldLayer The {@link Layer} the composition is currently in
     * @param newLayer The {@link Layer} to move the composition to
     * @param newtime The new start time of the composition
     * @throws OverlapException If the composition overlaps with another composition in the new layer
     */
    public void moveComposition(WrappedComposition composition, Layer oldLayer, Layer newLayer, long newtime) throws OverlapException, IllegalArgumentException {
        newLayer.canMoveThrows(composition, newtime);
        newLayer.add(composition.getComposition(), newtime, composition.getDuration());
        oldLayer.remove(composition);
    }

    /**
     * @return The duration of the cinematic in milliseconds
     */
    public long getDurationInMillis() {
        return duration;
    }

    /**
     * @return The duration of the cinematic
     */
    public Duration getDuration() {
        return Duration.ofMillis(duration);
    }

    /**
     * @return {@link CameraLayer}
     */
    public CameraLayer getCameraLayer() {
        return cameraLayer;
    }

    /**
     * @return The {@link Layer} at the given index
     */
    public Layer getLayer(int index) {
        return this.layers.get(index);
    }

    /**
     * @return The {@link Layer} with the given {@link UUID}
     */
    public @Nullable Layer getLayer(UUID layerUUID) {
        for (Layer layer : this.getLayers()) {
            if (layer.getUuid().equals(layerUUID)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Resolve a {@link Layer} by its index or {@link UUID}
     *
     * @param query The index (or "camera") or {@link UUID} of the layer (index starts at 1)
     * @return The {@link Layer} at the given index (starts at 1)
     */
    public @Nullable Layer resolveLayer(String query) {
        if (query.equalsIgnoreCase("camera")) {
            return this.cameraLayer;
        }
        try {
            int index = Integer.parseInt(query);
            return this.getLayer(index - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            try {
                UUID uuid = UUID.fromString(query);
                return this.getLayer(uuid);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return null;
    }

    /**
     * @return The amount of layers in the cinematic
     */
    public int getLayerCount() {
        return this.layers.size();
    }

    /**
     * Get the {@link Layer} and {@link Composition} by the given composition UUID
     *
     * @param compositionUUID The {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Layer} and the {@link Composition}, or null if the composition was not found.
     */
    public Pair<Layer, Composition> getLayerAndComposition(UUID compositionUUID) {
        WrappedComposition c = this.cameraLayer.findWrappedComposition(compositionUUID);
        if (c != null) {
            return new Pair<>(this.cameraLayer, c.getComposition());
        }

        for (Layer layer : this.layers) {
            c = layer.findWrappedComposition(compositionUUID);
            if (c != null) {
                return new Pair<>(layer, c.getComposition());
            }
        }
        return null;
    }

    public ArrayList<Layer> getLayers() {
        ArrayList<Layer> layers = new ArrayList<>(this.layers);
        layers.add(0, this.cameraLayer);
        return layers;
    }

    /**
     * Get the {@link Layer} and {@link WrappedComposition} by the given composition UUID
     *
     * @param compositionQuery The name or {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Layer} and the {@link WrappedComposition}, or null if the composition was not found.
     */
    public Pair<Layer, WrappedComposition> getLayerAndWrappedComposition(String compositionQuery) {
        WrappedComposition c = this.cameraLayer.findWrappedComposition(compositionQuery);
        if (c != null) {
            return new Pair<>(this.cameraLayer, c);
        }

        for (Layer layer : this.layers) {
            c = layer.findWrappedComposition(compositionQuery);
            if (c != null) {
                return new Pair<>(layer, c);
            }
        }
        return null;
    }

    public void tickOverlay(MatrixStack MatrixStack, long time) {
        for (int i = this.layers.size() - 1; i >= 0; i--) {  // loop reverse
            Layer layer = this.layers.get(i);

            Composition compo = layer.getComposition(time);
            if (!(compo instanceof OverlayComposition)) continue;

            ((OverlayComposition) compo).tick(MatrixStack, time);
        }
    }

    public void onCinematicLoad() {
        this.cameraLayer.onCinematicLoad();
        for (Layer layer : this.layers) {
            layer.onCinematicLoad();
        }
    }

    public void onCinematicUnload() {
        this.cameraLayer.onCinematicUnload();
        for (Layer layer : this.layers) {
            layer.onCinematicUnload();
        }
    }

    public void onStart() {
        this.cameraLayer.onStart();
        for (Layer layer : this.layers) {
            layer.onStart();
        }
    }

    public void onPause(long time) {
        this.cameraLayer.onPause(time);
        for (Layer layer : this.layers) {
            layer.onPause(time);
        }
    }

    public void onResume(long time) {
        this.cameraLayer.onResume(time);
        for (Layer layer : this.layers) {
            layer.onResume(time);
        }
    }

    public void onStop(long time) {
        this.cameraLayer.onStop(time);
        for (Layer layer : this.layers) {
            layer.onStop(time);
        }
    }

    public void onTimeChange(long oldTime, long time) {
        this.cameraLayer.onTimeChange(oldTime, time);
        for (Layer layer : this.layers) {
            layer.onTimeChange(oldTime, time);
        }
    }

    public void onTick(long lastTick, long time) {
        this.cameraLayer.onTick(lastTick, time);
        for (Layer layer : this.layers) {
            layer.onTick(lastTick, time);
        }
    }
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("uuid", this.uuid.toString());
        json.addProperty("name", this.name);
        json.addProperty("duration", this.duration);

        json.add("cameraLayer", this.cameraLayer.toJson());

        JsonArray overlayLayerJson = new JsonArray();
        for (Layer layer : this.layers) {
            overlayLayerJson.add(layer.toJson());
        }
        json.add("overlayLayers", overlayLayerJson);

        return json;
    }
    
    public static Cinematic fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);

        CameraLayer cameraLayer = (CameraLayer) Layer.fromJson(json.get("cameraLayer").getAsJsonObject(), CameraLayer.class);
        List<Layer> overlayLayer = new ArrayList<>();
        JsonArray overlayLayerJson = json.get("overlayLayers").getAsJsonArray();
        for (int i = 0; i < overlayLayerJson.size(); i++) {
            overlayLayer.add(Layer.fromJson(overlayLayerJson.get(i).getAsJsonObject()));
        }

        return new Cinematic(data.uuid(), data.name(), data.duration(), cameraLayer, overlayLayer);
    }

    public static Cinematic create(String name, long duration) {
        Cinematic cinematic = new Cinematic(UUID.randomUUID(), name, duration, new CameraLayer(), new ArrayList<>());
        cinematic.addLayer();
        var playerCamCompo = CameraComposition.create("default_camera", ECameraType.PLAYER);
        cinematic.cameraLayer.add(playerCamCompo, 0, duration);
        return cinematic;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
