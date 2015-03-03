package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by DKolodzey on 02.03.15.
 * class to work with multiple Filemaps
 */
public class MultiFileMap {

    private FileMap[][] fileMaps;
    private Path path;

    public MultiFileMap(Path path) throws IOException{
        this.path = path;
        this.fileMaps = new FileMap[16][16];
    }
}
