package ru.fizteh.fivt.students.Oktosha.database.filebackend;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by DKolodzey on 01.03.15.
 * class which implements functionality from file map (without user interaction)
 *
 * FileMap doesn't hold null in keys or values,
 * also you can add additional checks passing predicates to constructor
 */
public class FileMap {

    private Map<String, String> data;
    private Path path;
    private Predicate<String> badKeyPredicate;
    private Predicate<String> badValuePredicate;

    public FileMap(Path path) throws IOException {
        this(path, (s)->s == null, (s)->s == null);
    }

    public FileMap(Path path,
                   Predicate<String> badKeyPredicate,
                   Predicate<String> badValuePredicate) throws IOException {

        this.path = path;
        this.badKeyPredicate = (badKeyPredicate == null)
                ? (s) -> s == null
                : (s) -> s == null || badKeyPredicate.test(s);
        this.badValuePredicate = (badValuePredicate == null)
                ? (s) -> s == null
                : (s) -> s == null || badValuePredicate.test(s);

        if (path.toFile().exists()) {
            if (path.toFile().isFile()) {
                this.data = readAll();
            } else {
                throw new IOException("bd file is not a file: " + path.toString());
            }
        } else {
            this.data = new HashMap<>();
        }
    }

    public void save() throws IOException {
        if (size() == 0) {
            Files.deleteIfExists(path);
        } else {
            writeAll();
        }
    }

    public void clear() {
        data.clear();
    }

    public String put(String key, String value) {
        if (badKeyPredicate.test(key) || badValuePredicate.test(value)) {
            throw new IllegalArgumentException();
        }
        return data.put(key, value);
    }

    public String get(String key) {
        if (badKeyPredicate.test(key)) {
            throw new IllegalArgumentException();
        }
        return data.get(key);
    }

    public String remove(String key) {
        if (badKeyPredicate.test(key)) {
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

    private static void writeWord(String word, DataOutputStream outputStream) throws IOException {
        byte[] byteWord = word.getBytes("utf-8");
        outputStream.writeInt(byteWord.length);
        outputStream.write(byteWord);
    }

    private void writeAll() throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path.toFile()))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writeWord(entry.getKey(), outputStream);
                writeWord(entry.getValue(), outputStream);
            }
        }
    }

    private static String readWord(DataInputStream inputStream) throws IOException {
        byte[] firstBytes = new byte[4];
        try {
            firstBytes[0] = inputStream.readByte();
        } catch (EOFException e) {
            return null;
        }
        try {
            inputStream.readFully(firstBytes, 1, 3);
            int wordLength = ByteBuffer.wrap(firstBytes).getInt();
            if (wordLength < 0) {
                throw new IOException("bd file is broken");
            }
            ByteArrayOutputStream byteWord = new ByteArrayOutputStream();
            for (int i = 0; i < wordLength; ++i) {
                byteWord.write(inputStream.readByte());
            }
            return byteWord.toString("utf-8");
        } catch (EOFException e) {
            throw new IOException("bd file is broken", e);
        }
    }

    private Map<String, String> readAll() throws IOException {
        Map<String, String> dataFromFile = new HashMap<>();
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(path.toFile()))) {
           for (String key = readWord(inputStream); key != null; key = readWord(inputStream)) {
               String value = readWord(inputStream);
               if (badKeyPredicate.test(key) || badValuePredicate.test(value)) {
                   throw new IOException("bd file is broken: " + path.toString());
               }
               dataFromFile.put(key, value);
           }
        }
        return dataFromFile;
    }
}
