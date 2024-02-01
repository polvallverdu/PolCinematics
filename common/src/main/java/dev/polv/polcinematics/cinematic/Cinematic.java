package dev.polv.polcinematics.cinematic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.internal.compositions.Composition;
import dev.polv.polcinematics.internal.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.internal.compositions.types.camera.ECameraType;
import dev.polv.polcinematics.internal.layers.CameraLayer;
import dev.polv.polcinematics.internal.layers.Layer;
import dev.polv.polcinematics.internal.layers.WrappedComposition;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinematic extends Timeline {

    private final CameraLayer cameraLayer;

    protected Cinematic(UUID uuid, String name, long duration, CameraLayer cameraLayer, List<Layer> layers) {
        super(uuid, name, duration, layers);
        this.cameraLayer = cameraLayer;
    }

    /**
     * @return {@link CameraLayer}
     */
    public CameraLayer getCameraLayer() {
        return cameraLayer;
    }

    /**
     * @return The {@link Layer} with the given {@link UUID}
     */
    @Override
    public @Nullable Layer getLayer(UUID layerUUID) {
        if (this.cameraLayer.getUuid().equals(layerUUID)) {
            return this.cameraLayer;
        }
        return super.getLayer(layerUUID);
    }

    /**
     * Resolve a {@link Layer} by its index or {@link UUID}
     *
     * @param query The index (or "camera") or {@link UUID} of the layer (index starts at 1)
     * @return The {@link Layer} at the given index (starts at 1)
     */
    @Override
    public @Nullable Layer resolveLayer(String query) {
        if (query.equalsIgnoreCase("camera")) {
            return this.cameraLayer;
        }

        return super.resolveLayer(query);
    }

    /**
     * Get the {@link Layer} and {@link Composition} by the given composition UUID
     *
     * @param compositionUUID The {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Layer} and the {@link Composition}, or null if the composition was not found.
     */
    @Override
    public Pair<Layer, Composition> getLayerAndComposition(UUID compositionUUID) {
        WrappedComposition c = this.cameraLayer.findWrappedComposition(compositionUUID);
        if (c != null) {
            return new Pair<>(this.cameraLayer, c.getComposition());
        }

        return super.getLayerAndComposition(compositionUUID);
    }

    @Override
    public ArrayList<Layer> getLayers() {
        ArrayList<Layer> layers = super.getLayers();
        layers.add(0, this.cameraLayer);
        return layers;
    }

    /**
     * Get the {@link Layer} and {@link WrappedComposition} by the given composition UUID
     *
     * @param compositionQuery The name or {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Layer} and the {@link WrappedComposition}, or null if the composition was not found.
     */
    @Override
    public Pair<Layer, WrappedComposition> getLayerAndWrappedComposition(String compositionQuery) {
        WrappedComposition c = this.cameraLayer.findWrappedComposition(compositionQuery);
        if (c != null) {
            return new Pair<>(this.cameraLayer, c);
        }

        return super.getLayerAndWrappedComposition(compositionQuery);
    }

    @Override
    public boolean removeLayer(Layer layer) {
        if (layer == this.cameraLayer) {
            return false;
        }
        return super.removeLayer(layer);
    }

    @Override
    public void onTimelineLoad() {
        this.cameraLayer.onTimelineLoad();
        super.onTimelineLoad();
    }

    @Override
    public void onTimelineUnload() {
        this.cameraLayer.onTimelineUnload();
        super.onTimelineUnload();
    }

    @Override
    public void onStart() {
        this.cameraLayer.onStart();
        super.onStart();
    }

    @Override
    public void onPause(long time) {
        this.cameraLayer.onPause(time);
        super.onPause(time);
    }

    @Override
    public void onResume(long time) {
        this.cameraLayer.onResume(time);
        super.onResume(time);
    }

    @Override
    public void onStop(long time) {
        this.cameraLayer.onStop(time);
        super.onStop(time);
    }

    @Override
    public void onTimeChange(long oldTime, long time) {
        this.cameraLayer.onTimeChange(oldTime, time);
        super.onTimeChange(oldTime, time);
    }

    @Override
    public void onTick(long lastTick, long time) {
        this.cameraLayer.onTick(lastTick, time);
        super.onTick(lastTick, time);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();

        json.add("cameraLayer", this.cameraLayer.toJson());

        return json;
    }

    public static Cinematic fromJson(JsonObject json) { // CRITICAL: can generate conflicts in a future
        BasicCompositionData data = BasicCompositionData.fromJson(json);

        CameraLayer cameraLayer = (CameraLayer) Layer.fromJson(json.get("cameraLayer").getAsJsonObject(), CameraLayer.class);
        List<Layer> overlayLayer = new ArrayList<>();
        JsonArray overlayLayerJson = json.get("overlayLayers").getAsJsonArray();
        for (int i = 0; i < overlayLayerJson.size(); i++) {
            overlayLayer.add(Layer.fromJson(overlayLayerJson.get(i).getAsJsonObject()));
        }

        return new Cinematic(data.uuid(), data.name(), data.duration(), cameraLayer, overlayLayer);
    }

    public static Cinematic create(String name, long duration) { // CRITICAL: can generate conflicts in a future
        Cinematic cinematic = new Cinematic(UUID.randomUUID(), name, duration, new CameraLayer(), new ArrayList<>());
        cinematic.addLayer();
        var playerCamCompo = CameraComposition.create("default_camera", ECameraType.PLAYER);
        cinematic.cameraLayer.add(playerCamCompo, 0, duration);
        return cinematic;
    }

}
