package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOError;
import java.io.IOException;
import java.util.*;

/**
 * Created by DKolodzey on 04.03.15.
 * Implementation if TableWithDiff (advanced Table)
 */
public class TableWithDiffImpl implements TableWithDiff {

    private MultiFileMap multiFileMap;
    private final ThreadLocal<Map<String, String>> diff = new ThreadLocal<>();
    private final ThreadLocal<Boolean> tableIsDropped = new ThreadLocal<>();

    public TableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff.set(new HashMap<>());
        this.tableIsDropped.set(false);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        return diff.get().size();
    }

    @Override
    public String getName() {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        return multiFileMap.getName();
    }

    @Override
    public String get(String key) {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (diff.get().containsKey(key)) {
            return diff.get().get(key);
        } else {
            return multiFileMap.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.get().put(key, value);
        return ret;
    }

    @Override
    public String remove(String key) {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.get().put(key, null);
        return ret;
    }

    @Override
    public int size() {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        int ret = multiFileMap.size();
        for (Map.Entry<String, String> entry : diff.get().entrySet()) {
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
    public int commit()  {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        int ret = getNumberOfUncommittedChanges();
        for (Map.Entry<String, String> entry : diff.get().entrySet()) {
            if (entry.getValue() == null) {
                multiFileMap.remove(entry.getKey());
            } else {
                multiFileMap.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            multiFileMap.save();
        } catch (IOException e) {
            throw new IOError(e);
        }
        diff.get().clear();
        return ret;
    }

    @Override
    public int rollback() {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        int ret = getNumberOfUncommittedChanges();
        diff.get().clear();
        return ret;
    }

    @Override
    public List<String> list() {
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        Set<String> keySet = new HashSet<>(multiFileMap.list());
        for (Map.Entry<String, String> entry : diff.get().entrySet()) {
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
        if (tableIsDropped.get()) {
            throw new IllegalStateException();
        }
        multiFileMap.clear();
        multiFileMap.save();
        diff.get().clear();
        tableIsDropped.set(true);
    }
}
