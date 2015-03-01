package ru.fizteh.fivt.students.Oktosha;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DKolodzey on 01.03.15.
 * class which implements functionality from file map (without user interaction)
 */
public class FileMap {
    public FileMap() {
        data = new HashMap<>();
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
    public int size() {
        return data.size();
    }
    public List<String> list() {
        return new ArrayList<>(data.keySet());
    }

    private Map<String, String> data;

    private void writeWord(String word, DataOutputStream outputStream) throws IOException {
        byte[] byteWord = word.getBytes("utf-8");
        outputStream.writeInt(byteWord.length);
        outputStream.write(byteWord);
    }

    private void writeAll(Path path) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path.toFile()))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writeWord(entry.getKey(), outputStream);
                writeWord(entry.getValue(), outputStream);
            }
        }
    }
}
