package engineer.pol.cinematic.compositions.core.attributes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.Composition;
import it.unimi.dsi.fastutil.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AttributeList {

    private final Composition parent;
    private final HashMap<String, Attribute> attributesHashMap;

    private AttributeList(Composition parent, HashMap<String, Attribute> attributesHashMap) {
        this.parent = parent;
        this.attributesHashMap = attributesHashMap;
    }

    public AttributeList(Composition parent) {
        this(parent, new HashMap<>());
    }

    public List<Attribute> getAttributes() {
        return new ArrayList<>(attributesHashMap.values());
    }

    public Attribute getAttribute(String name) {
        return attributesHashMap.get(name);
    }

    public void addAttribute(Attribute attribute) {
        attributesHashMap.put(attribute.getName(), attribute);
    }

    public Attribute createAttribute(String name, EAttributeType type) {
        Attribute attribute = new Attribute(UUID.randomUUID(), name, parent, type, new ArrayList<>());
        attributesHashMap.put(name, attribute);
        return attribute;
    }

    public void removeAttribute(String name) {
        attributesHashMap.remove(name);
    }

    public void removeAttribute(Attribute attribute) {
        attributesHashMap.remove(attribute.getName());
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        for (Attribute attribute : attributesHashMap.values()) {
            jsonArray.add(attribute.toJson());
        }
        jsonObject.add("attributes", jsonArray);

        return jsonObject;
    }

    public static AttributeList fromJson(JsonObject json, Composition parent) {
        JsonArray jsonArray = json.getAsJsonArray("attributes");

        HashMap<String, Attribute> attributesHashMap = new HashMap<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            Attribute attribute = Attribute.fromJson(jsonArray.get(i).getAsJsonObject(), parent);
            attributesHashMap.put(attribute.getName(), attribute);
        }

        return new AttributeList(parent, attributesHashMap);
    }
}
