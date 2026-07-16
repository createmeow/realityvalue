package dev.anye.mc.reality_value.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class _JsonSupport {
    public static void CheckData(String sourceJson, String targetJsonFilePath) {
        try {
            JsonElement sourceJsonElement = JsonParser.parseString(sourceJson);
            JsonElement targetJsonElement = readJsonFromFile(targetJsonFilePath);
            JsonElement mergedJsonElement = mergeJsonElements(sourceJsonElement, targetJsonElement);
            writeJsonToFile(mergedJsonElement, targetJsonFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonElement readJsonFromFile(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return JsonParser.parseReader(reader);
        }
    }

    private static void writeJsonToFile(JsonElement jsonElement, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonElement.toString());
        }
    }

    private static JsonElement mergeJsonElements(JsonElement sourceElement, JsonElement targetElement) {
        if (sourceElement.isJsonObject() && targetElement.isJsonObject()) {
            JsonObject sourceObj = sourceElement.getAsJsonObject();
            JsonObject targetObj = targetElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : sourceObj.entrySet()) {
                String key = entry.getKey();
                JsonElement valueSource = entry.getValue();
                if (targetObj.has(key)) {
                    JsonElement valueTarget = targetObj.get(key);
                    targetObj.add(key, mergeJsonElements(valueSource, valueTarget));
                } else {
                    targetObj.add(key, valueSource);
                }
            }
            return targetObj;
        } else if (sourceElement.isJsonArray() && targetElement.isJsonArray()) {
            JsonArray sourceArray = sourceElement.getAsJsonArray();
            JsonArray targetArray = targetElement.getAsJsonArray();
            if (targetArray.isEmpty()) {
                for (JsonElement element : sourceArray) {
                    targetArray.add(element);
                }
            }
            return targetArray;
        } else if (targetElement.isJsonNull()
                || (targetElement.isJsonArray() && targetElement.getAsJsonArray().isEmpty())) {
            return sourceElement;
        } else {
            return targetElement;
        }
    }
}