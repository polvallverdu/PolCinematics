package dev.polv.polcinematics.internal.compositions.values.timevariables;

import com.google.gson.JsonArray;
import dev.polv.polcinematics.internal.compositions.values.EValueType;
import dev.polv.polcinematics.internal.compositions.values.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

    public TimeVariable createTimeVariable(String name, String description, EValueType type, Object defaultValue) {
        TimeVariable timeVariable = new TimeVariable(UUID.randomUUID(), name, description, type, Stream.of(new Keyframe(0, new Value(defaultValue, type))).collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
        timeVariables.put(name, timeVariable);
        return timeVariable;
    }

    public List<String> getKeys() {
        return new ArrayList<>(timeVariables.keySet());
    }

    public JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();

        for (TimeVariable timeVariable : timeVariables.values()) {
            jsonArray.add(timeVariable.toJson());
        }

        return jsonArray;
    }

    public static CompositionTimeVariables fromJson(JsonArray jsonArray) {
        HashMap<String, TimeVariable> timevariables = new HashMap<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            TimeVariable timeVariable = TimeVariable.fromJson(jsonArray.get(i).getAsJsonObject());
            timevariables.put(timeVariable.getName(), timeVariable);
        }

        return new CompositionTimeVariables(timevariables);
    }
}
