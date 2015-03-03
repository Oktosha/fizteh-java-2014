package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKolodzey on 02.03.15.
 * class to work with multiple Filemaps
 */
public class MultiFileMap {

    private FileMap[][] fileMaps;
    private Path path;

    public MultiFileMap(Path path) throws IOException {
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

    public void save() throws IOException {
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            Path directoryPath = path.resolve(FileMapPosition.relPathToDirectory(directoryId));
            if (directorySize(directoryId) > 0) {
                if (!directoryPath.toFile().exists()) {
                    Files.createDirectory(directoryPath);
                }
            }
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                fileMaps[directoryId][fileId].save();
            }
            if ((directorySize(directoryId) == 0)) {
                try {
                    Files.deleteIfExists(directoryPath);
                } catch (DirectoryNotEmptyException ignored) {
                    /* if bd dir contains user files, we don't delete it */
                }
            }
        }
    }

    public void clear() {
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                fileMaps[directoryId][fileId].clear();
            }
        }
    }

    public String put(String key, String value) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].put(key, value);
    }

    public  String get(String key) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].get(key);
    }

    public String remove(String key) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].remove(key);
    }

    public int size() {
        int ans = 0;
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            ans += directorySize(directoryId);
        }
        return ans;
    }

    private int directorySize(int directoryId) {
        int ans = 0;
        for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
            ans += fileMaps[directoryId][fileId].size();
        }
        return ans;
    }

    public List<String> list() {
        List<String> ans = new ArrayList<String>();
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                ans.addAll(fileMaps[directoryId][fileId].list());
            }
        }
        return ans;
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

    public int getDirectoryId() {
        return directoryId;
    }

    public int getFileId() {
        return fileId;
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
