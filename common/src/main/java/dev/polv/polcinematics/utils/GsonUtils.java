package dev.polv.polcinematics.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class GsonUtils {

    public static Gson gson = new Gson();

    public static JsonObject jsonFromString(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static JsonObject jsonFromFile(File file) throws IOException {
        String content = readFromFile(file);
        return jsonFromString(content);
    }

    public static String jsonToString(JsonObject json) {
        return json.toString();
    }

    private static String readFromFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } finally {
            inputStream.close();
        }
        return resultStringBuilder.toString();
    }
}
