package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
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

    private Map<String, String> data;
    private Path path;

    public FileMap(Path path) throws IOException {
        this.path = path;
        if (path.toFile().exists()) {
            if (path.toFile().isFile()) {
                this.data = readAll(path);
            } else {
                throw new IOException("bd file is not a file: " + path.toString());
            }
        } else {
            this.data = new HashMap<>();
        }
    }

    public void save() throws IOException {
        if (size() == 0)
            Files.deleteIfExists(path);
        else
            writeAll(path, data);
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

    private static void writeWord(String word, DataOutputStream outputStream) throws IOException {
        byte[] byteWord = word.getBytes("utf-8");
        outputStream.writeInt(byteWord.length);
        outputStream.write(byteWord);
    }

    private static void writeAll(Path path, Map<String, String> dataToFile) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path.toFile()))) {
            for (Map.Entry<String, String> entry : dataToFile.entrySet()) {
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

    private static Map<String, String> readAll(Path path) throws IOException {
        Map<String, String> dataFromFile = new HashMap<>();
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(path.toFile()))) {
           for (String key = readWord(inputStream); key != null; key = readWord(inputStream)) {
               String value = readWord(inputStream);
               if (value == null) {
                   throw new IOException("bd file is broken: " + path.toString());
               }
               dataFromFile.put(key, value);
           }
        }
        return dataFromFile;
    }
}
