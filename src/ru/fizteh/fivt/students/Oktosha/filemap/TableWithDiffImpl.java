package ru.fizteh.fivt.students.Oktosha.filemap;

import java.util.*;

/**
 * Created by DKolodzey on 04.03.15.
 */
public class TableWithDiffImpl implements TableWithDiff {

    private MultiFileMap multiFileMap;
    private Map<String, String> diff;

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
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        diff.put(key, value);
        return null;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.put(key, null);
        return ret;
    }

    @Override
    public int size() {
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
        int ret = getNumberOfUncommittedChanges();
        diff.clear();
        return ret;
    }

    @Override
    public List<String> list() {
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
}
