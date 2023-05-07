package dev.polv.polcinematics.cinematic.compositions.timevariables;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.value.EValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompositionTimeVariables {

    private final HashMap<String, TimeVariable> timeVariables;

    private CompositionTimeVariables(HashMap<String, TimeVariable> timeVariables) {
        this.timeVariables = timeVariables;
    }

    public CompositionTimeVariables() {
        this(new HashMap<>());
    }

    public List<TimeVariable> getTimeVariables() {
        return new ArrayList<>(timeVariables.values());
    }

    public TimeVariable getTimeVariables(String name) {
        return timeVariables.get(name);
    }

    public TimeVariable createTimeVariable(String name, String description, EValueType type) {
        TimeVariable timeVariable = new TimeVariable(UUID.randomUUID(), name, description, type, new ArrayList<>());
        timeVariables.put(name, timeVariable);
        return timeVariable;
    }

    public List<String> getKeys() {
        return new ArrayList<>(timeVariables.keySet());
    }

    public JsonObject toJson() {
        JsonArray jsonArray = new JsonArray();
        for (TimeVariable timeVariable : timeVariables.values()) {
            jsonArray.add(timeVariable.toJson());
        }

        return jsonArray.getAsJsonObject();
    }

    public static CompositionTimeVariables fromJson(JsonObject json) {
        JsonArray jsonArray = json.getAsJsonArray();

        HashMap<String, TimeVariable> timevariables = new HashMap<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            TimeVariable timeVariable = TimeVariable.fromJson(jsonArray.get(i).getAsJsonObject());
            timevariables.put(timeVariable.getName(), timeVariable);
        }

        return new CompositionTimeVariables(timevariables);
    }
}
