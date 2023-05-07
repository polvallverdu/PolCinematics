package dev.polv.polcinematics.cinematic.compositions.values.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.cinematic.compositions.values.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompositionConstants {

    private final HashMap<String, Constant> constantValues;

    public CompositionConstants() {
        this(new HashMap<>());
    }

    public CompositionConstants(HashMap<String, Constant> constantVariables) {
        this.constantValues = constantVariables;
    }

    public List<Constant> getConstants() {
        return new ArrayList<>(constantValues.values());
    }

    public Constant getConstant(String key) {
        return constantValues.get(key);
    }

    public List<String> getKeys() {
        return new ArrayList<>(constantValues.keySet());
    }


    public Constant createConstant(String key, String description, EValueType type, Object defaultValue) {
        Constant constant = new Constant(UUID.randomUUID(), key, description, new Value(defaultValue, type));
        constantValues.put(key, constant);
        return constant;
    }

    public JsonObject toJson() {
        JsonArray jsonArray = new JsonArray();

        for (Constant constant : constantValues.values()) {
            jsonArray.add(constant.toJson());
        }

        return jsonArray.getAsJsonObject();
    }

    public static CompositionConstants fromJson(JsonObject json) {
        JsonArray jsonArray = json.getAsJsonArray();

        HashMap<String, Constant> constants = new HashMap<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            Constant constant = Constant.fromJson(jsonArray.get(i).getAsJsonObject());
            constants.put(constant.getKey(), constant);
        }

        return new CompositionConstants(constants);
    }

}
