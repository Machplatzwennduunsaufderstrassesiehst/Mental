package de.soeiner.mental.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sven on 22.06.16.
 */
public class JSONObject extends HashMap<String, Object> {

    public JSONObject() {

    }

    public JSONObject(String jsonString) {
        jsonString = jsonString.trim();
        if (jsonString.charAt(0) != '{' && jsonString.charAt(jsonString.length() - 1) != '}') {
            throw new JSONException("Invalid JSON data");
        }
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        System.out.println("Cut json string: " + jsonString);
        String[] keyValueArray = jsonString.split(",");
        for (String keyValuePair : keyValueArray) {
            System.out.println("KV Pair: " + keyValuePair);
            String key = keyValuePair.split(":")[0];
            key = key.replaceAll("\"", "");
            System.out.println(key);
            String value = keyValuePair.split(":")[1];
            value = value.trim();
            if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
                value = value.substring(1, value.length() - 1);
            }
            System.out.println(value);
            this.put(key, value);
        }
    }

    @Override
    public String toString() {
        String s = "{";
        Iterator<String> keys = this.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String keyString = "\"" + key + "\"";
            String valueString = this.get(key).toString();
            s += keyString + ":" + valueString;
            if (keys.hasNext()) s += ",";
        }
        return s + "}";
    }

    public boolean getBoolean(String key) {
        if (!this.containsKey(key)) throw new JSONException("key not found");
        try {
            return Boolean.getBoolean(this.get(key).toString());
        } catch (Exception e) {
            throw new JSONException("not a boolean");
        }
    }

    public int getInt(String key) {
        if (!this.containsKey(key)) throw new JSONException("key not found");
        try {
            return Integer.parseInt(this.get(key).toString());
        } catch (Exception e) {
            throw new JSONException("not an int");
        }
    }

    public JSONObject getJSONObject(String key) {
        if (!this.containsKey(key)) throw new JSONException("key not found");
        return new JSONObject(this.get(key).toString());
    }

    public JSONArray getJSONArray(String key) {
        if (!this.containsKey(key)) throw new JSONException("key not found");
        return new JSONArray(this.get(key).toString());
    }

    public String getString(String key) {
        if (!this.containsKey(key)) throw new JSONException("key not found");
        return this.get(key).toString().replaceAll("\"", "");
    }

    public boolean has(String key) {
        return this.containsKey(key);
    }

    public void put(String key, JSONArray array) {
        this.put(key, (Object) array);
    }

    public void put(String key, String value) {
        Object object = "\"" + value + "\"";
        this.put(key, object);
    }
}


