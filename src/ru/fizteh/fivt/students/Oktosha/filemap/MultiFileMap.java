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
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            Path directoryPath = path.resolve(FileMapPosition.relPathToDirectory(directoryId));
            if (directoryPath.toFile().exists() && !directoryPath.toFile().isDirectory()) {
                throw new IOException("bd dir is not a dir: " + directoryPath.toString());
            }
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                Path fileMapPath = path.resolve(FileMapPosition.relPathToFileMap(directoryId, fileId));
                fileMaps[directoryId][fileId] = new FileMap(fileMapPath);
            }
        }
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
        return relPathToFileMap(directoryId, fileId);
    }
    Path relPathToDirectory() {
        return relPathToDirectory(directoryId);
    }
    static Path relPathToDirectory(int directoryId) {
        return Paths.get(String.format("%d.dir", directoryId));
    }
    static Path relPathToFileMap(int directoryId, int fileId) {
        return Paths.get(String.format("%d.dir/%d.dat", directoryId, fileId));
    }
}
