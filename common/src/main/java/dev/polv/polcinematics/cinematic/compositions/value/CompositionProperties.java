package dev.polv.polcinematics.cinematic.compositions.value;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompositionProperties {

    private final HashMap<String, Value> propertiesHashMap;
    private final List<String> keys;

    public CompositionProperties() {
        this.propertiesHashMap = new HashMap<>();
        this.keys = new ArrayList<>();
    }

    public CompositionProperties(HashMap<String, Value> propertiesHashMap) {
        this.propertiesHashMap = propertiesHashMap;
        this.keys = new ArrayList<>(propertiesHashMap.keySet());
    }

    public List<Value> getValues() {
        return new ArrayList<>(propertiesHashMap.values());
    }

    public List<String> getKeys() {
        return new ArrayList<>(keys);
    }

    public Value getValue(String key) {
        return propertiesHashMap.get(key);
    }

    public Value createProperty(String key, EValueType type, Object v) {
        Value value = new Value(v, type);
        propertiesHashMap.put(key, value);
        keys.add(key);
        return value;
    }

    public void removeProperty(String key) {
        propertiesHashMap.remove(key);
        keys.remove(key);
    }

    public void removeProperty(Value value) {
        String key = getKey(value);
        if (key != null) {
            propertiesHashMap.remove(key);
            keys.remove(key);
        }
    }

    public String getKey(Value value) {
        for (String key : keys) {
            if (propertiesHashMap.get(key) == value) {
                return key;
            }
        }
        return null;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        for (String key : keys) {
            jsonObject.add(key, propertiesHashMap.get(key).toJson());
        }

        return jsonObject;
    }

    public static CompositionProperties fromJson(JsonObject jsonObject) {
        HashMap<String, Value> propertiesHashMap = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            propertiesHashMap.put(key, Value.fromJson(jsonObject.get(key).getAsJsonObject()));
        }
        return new CompositionProperties(propertiesHashMap);
    }

}
