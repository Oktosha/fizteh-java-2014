package ru.fizteh.fivt.students.Oktosha.filemap;

import java.util.List;
import java.util.Map;

/**
 * Created by DKolodzey on 04.03.15.
 */
public class TableWithDiffImpl implements TableWithDiff {

    private MultiFileMap multiFileMap;
    private Map<String, String> diff;
    private int sizeDifference;

    @Override
    public int getNumberOfUncommittedChanges() {
        return diff.size();
    }

    @Override
    public String getName() {
        return multiFileMap.getName();
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String put(String key, String value) {
        return null;
    }

    @Override
    public String remove(String key) {
        return null;
    }

    @Override
    public int size() {
        return multiFileMap.size() + sizeDifference;
    }

    @Override
    public int commit() {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    @Override
    public List<String> list() {
        return null;
    }
}
