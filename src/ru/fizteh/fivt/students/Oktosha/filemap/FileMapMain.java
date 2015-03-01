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
        System.out.println(filemap.put("key", "value"));
        System.out.println(filemap.put("ключ", "значение"));
        filemap.save();
    }
}
