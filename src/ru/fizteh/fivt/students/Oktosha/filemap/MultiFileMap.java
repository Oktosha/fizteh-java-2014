package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by DKolodzey on 02.03.15.
 * class to work with multiple Filemaps
 */
public class MultiFileMap {

    private FileMap[][] fileMaps;
    private Path path;

    public MultiFileMap(Path path) throws IOException{
        this.path = path;
        this.fileMaps = new FileMap[FileMapPosition.DIR_PER_TABLE][FileMapPosition.FILES_PER_DIR];
    }

}

class FileMapPosition {
    static final int DIR_PER_TABLE = 16;
    static final int FILES_PER_DIR = 16;
    private int directoryId;
    private int fileId;
    FileMapPosition(int directoryId, int fileId) {
        this.directoryId = directoryId;
        this.fileId = fileId;
    }
    FileMapPosition(String key) {
        this(key.hashCode() % DIR_PER_TABLE, key.hashCode() / DIR_PER_TABLE % FILES_PER_DIR);
    }
    Path relPathToFileMap() {
        return Paths.get(String.format("%d.dir/%d.dat", directoryId, fileId));
    }
}
