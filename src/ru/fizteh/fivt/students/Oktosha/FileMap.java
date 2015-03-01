package ru.fizteh.fivt.students.Oktosha;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DKolodzey on 01.03.15.
 * class which implements functionality from file map (without user interaction)
 */
public class FileMap {
    public FileMap() {
        data = new HashMap<String, String>();
    }
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return data.put(key, value);
    }
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return data.get(key);
    }
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return data.remove(key);
    }
    private Map<String, String> data;
}
