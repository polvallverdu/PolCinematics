package engineer.pol.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;

public class GsonUtils {

    public static Gson gson = new Gson();

    // create an empty JsonObject
    public static JsonObject createJsonObject() {
        return new JsonObject();
    }

    // load json from string
    public static JsonObject loadJsonObject(String json) {
        return gson.fromJson(json, JsonObject.class);
    }

    public static String toJson(JsonObject json) {
        return gson.toJson(json);
    }

    public static byte[] encodeJsonObject(JsonObject json) {
        return gson.toJson(json).getBytes();
    }
}
