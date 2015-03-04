package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOException;
import java.util.*;

/**
 * Created by DKolodzey on 04.03.15.
 * Implementation if TableWithDiff (advanced Table)
 */
public class TableWithDiffImpl implements TableWithDiff {

    private MultiFileMap multiFileMap;
    private Map<String, String> diff;
    private boolean tableIsDropped;

    public TableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff = new HashMap<>();
        this.tableIsDropped = false;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        return diff.size();
    }

    @Override
    public String getName() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        return multiFileMap.getName();
    }

    @Override
    public String get(String key) {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (diff.containsKey(key)) {
            return diff.get(key);
        } else {
            return multiFileMap.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        diff.put(key, value);
        return null;
    }

    @Override
    public String remove(String key) {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.put(key, null);
        return ret;
    }

    @Override
    public int size() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        int ret = multiFileMap.size();
        for (Map.Entry<String, String> entry : diff.entrySet()) {
            if (entry.getValue() == null && multiFileMap.get(entry.getKey()) != null) {
                --ret;
            }
            if (entry.getValue() != null && multiFileMap.get(entry.getKey()) == null) {
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public int commit() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        int ret = getNumberOfUncommittedChanges();
        for (Map.Entry<String, String> entry : diff.entrySet()) {
            if (entry.getValue() == null) {
                multiFileMap.remove(entry.getKey());
            } else {
                multiFileMap.put(entry.getKey(), entry.getValue());
            }
        }
        diff.clear();
        return ret;
    }

    @Override
    public int rollback() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        int ret = getNumberOfUncommittedChanges();
        diff.clear();
        return ret;
    }

    @Override
    public List<String> list() {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        Set<String> keySet = new HashSet<>(multiFileMap.list());
        for (Map.Entry<String, String> entry : diff.entrySet()) {
            if (entry.getValue() == null) {
                keySet.remove(entry.getKey());
            } else {
                keySet.add(entry.getKey());
            }
        }
        return new ArrayList<>(keySet);
    }

    @Override
    public void drop() throws IOException {
        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        multiFileMap.clear();
        multiFileMap.save();
        diff.clear();
        tableIsDropped = true;
    }
}
