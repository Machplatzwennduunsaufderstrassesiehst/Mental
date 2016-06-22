package de.soeiner.mental.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sven on 22.06.16.
 */
public class JSONArray extends ArrayList<Object> {

    public JSONArray() {
        super();
    }

    public JSONArray(String jsonString) {
        super();
        jsonString = jsonString.trim();
        if (jsonString.charAt(0) != '[' && jsonString.charAt(jsonString.length() - 1) != ']') {
            throw new JSONException("Invalid JSON array data");
        }
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        String[] array = jsonString.split(",");
        for (int i = 0; i < array.length; i++) {
            this.set(i, array[i]);
        }
    }

    public JSONArray(Object[] array) {
        Collections.addAll(this, array);
    }

    public void put(Object object) {
        this.add(object);
    }

    public void put(int i, Object o) {
        this.set(i, o);
    }

}
