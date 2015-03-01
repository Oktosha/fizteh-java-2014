package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by DKolodzey on 01.03.15.
 * Simple main for filemap
 */
public class FileMapMain {
    public static void main(String[] args) throws IOException{
        FileMap filemap = new FileMap(Paths.get("tmp.txt"));
        filemap.put("key", "value");
        filemap.put("ключ", "значение");
    }
}
