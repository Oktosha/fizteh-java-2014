package ru.fizteh.fivt.students.Oktosha.database.filebackend;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKolodzey on 02.03.15.
 * class to work with multiple FileMaps
 */
public class MultiFileMapImpl implements MultiFileMap {

    private final FileMap[][] fileMaps;
    private Path path;

    public MultiFileMapImpl(Path path) throws IOException {
        this.path = path;
        this.fileMaps = new FileMap[FileMapPosition.DIR_PER_TABLE][FileMapPosition.FILES_PER_DIR];
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            Path directoryPath = path.resolve(FileMapPosition.relPathToDirectory(directoryId));
            if (directoryPath.toFile().exists() && !directoryPath.toFile().isDirectory()) {
                throw new IOException("bd dir is not a dir: " + directoryPath.toString());
            }
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                FileMapPosition pos = new FileMapPosition(directoryId, fileId);
                Path fileMapPath = path.resolve(pos.relPathToFileMap());
                fileMaps[directoryId][fileId] = new FileMap(fileMapPath, (s)->!pos.isHoldingKey(s), (s)->s == null);
            }
        }
    }

    @Override
    public String getName() {
        return path.getFileName().toString();
    }

    @Override
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

    @Override
    public void clear() {
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                fileMaps[directoryId][fileId].clear();
            }
        }
    }

    @Override
    public String put(String key, String value) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].put(key, value);
    }

    @Override
    public  String get(String key) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].get(key);
    }

    @Override
    public String remove(String key) {
        FileMapPosition pos = new FileMapPosition(key);
        return fileMaps[pos.getDirectoryId()][pos.getFileId()].remove(key);
    }

    @Override
    public int size() {
        int ret = 0;
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            ret += directorySize(directoryId);
        }
        return ret;
    }

    private int directorySize(int directoryId) {
        int ret = 0;
        for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
            ret += fileMaps[directoryId][fileId].size();
        }
        return ret;
    }

    @Override
    public List<String> list() {
        List<String> ret = new ArrayList<>();
        for (int directoryId = 0; directoryId < FileMapPosition.DIR_PER_TABLE; ++directoryId) {
            for (int fileId = 0; fileId < FileMapPosition.FILES_PER_DIR; ++fileId) {
                ret.addAll(fileMaps[directoryId][fileId].list());
            }
        }
        return ret;
    }
}

class FileMapPosition {
    static final int DIR_PER_TABLE = 16;
    static final int FILES_PER_DIR = 16;
    private final int directoryId;
    private final int fileId;

    public FileMapPosition(int directoryId, int fileId) {
        this.directoryId = directoryId;
        this.fileId = fileId;
    }

    public FileMapPosition(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        this.directoryId = Integer.remainderUnsigned(key.hashCode(), DIR_PER_TABLE);
        this.fileId = Integer.divideUnsigned(key.hashCode(), DIR_PER_TABLE) % FILES_PER_DIR;
    }

    public int getDirectoryId() {
        return directoryId;
    }

    public int getFileId() {
        return fileId;
    }

    public Path relPathToFileMap() {
        return relPathToFileMap(directoryId, fileId);
    }

    static Path relPathToDirectory(int directoryId) {
        return Paths.get(String.format("%d.dir", directoryId));
    }

    static Path relPathToFileMap(int directoryId, int fileId) {
        return Paths.get(String.format("%d.dir/%d.dat", directoryId, fileId));
    }

    public boolean isHoldingKey(String key) {
        FileMapPosition other = new FileMapPosition(key);
        return other.directoryId == this.directoryId && other.fileId == this.fileId;
    }
}
