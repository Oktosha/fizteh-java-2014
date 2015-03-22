package ru.fizteh.fivt.students.Oktosha.database.string;

import ru.fizteh.fivt.students.Oktosha.database.filebackend.MultiFileMap;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 04.03.15.
 * Implementation if TableWithDiff (advanced Table)
 */
public class StringTableWithDiffImpl implements StringTableWithDiff {

    private MultiFileMap multiFileMap;
    private final ThreadLocal<Map<String, String>> diff = new ThreadLocal<>();
    private final ReadWriteLock backEndRWL; /* protects reading/writing to files */

    private Map<String, String> getDiff() {
        return diff.get();
    }

    public StringTableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff.set(new HashMap<>());
        this.backEndRWL = new ReentrantReadWriteLock(true);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return getDiff().size();
    }

    @Override
    public String getName() {
        return multiFileMap.getName();
    }

    @Override
    public String get(String key) {
        try {
            backEndRWL.readLock().lock();
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (getDiff().containsKey(key)) {
                return getDiff().get(key);
            }
            return multiFileMap.get(key);
        } finally {
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public String put(String key, String value) {
        try {
            backEndRWL.readLock().lock();
            if (key == null || value == null) {
                throw new IllegalArgumentException();
            }
            String ret = get(key);
            getDiff().put(key, value);
            return ret;
        } finally {
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public String remove(String key) {
        try {
            backEndRWL.readLock().lock();
            if (key == null) {
                throw new IllegalArgumentException();
            }
            String ret = get(key);
            getDiff().put(key, null);
            return ret;
        } finally {
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public int size() {
        try {
            backEndRWL.readLock().lock();
            int ret = multiFileMap.size();
            for (Map.Entry<String, String> entry : getDiff().entrySet()) {
                if (entry.getValue() == null && multiFileMap.get(entry.getKey()) != null) {
                    --ret;
                }
                if (entry.getValue() != null && multiFileMap.get(entry.getKey()) == null) {
                    ++ret;
                }
            }
            return ret;
        } finally {
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        try {
            backEndRWL.writeLock().lock();
            int ret = getNumberOfUncommittedChanges();
            for (Map.Entry<String, String> entry : getDiff().entrySet()) {
                if (entry.getValue() == null) {
                    multiFileMap.remove(entry.getKey());
                } else {
                    multiFileMap.put(entry.getKey(), entry.getValue());
                }
            }
            multiFileMap.save();
            getDiff().clear();
            return ret;
        } finally {
            backEndRWL.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        int ret = getNumberOfUncommittedChanges();
        getDiff().clear();
        return ret;
    }

    @Override
    public List<String> list() {
        try {
            backEndRWL.readLock().lock();
            Set<String> keySet = new HashSet<>(multiFileMap.list());
            for (Map.Entry<String, String> entry : getDiff().entrySet()) {
                if (entry.getValue() == null) {
                    keySet.remove(entry.getKey());
                } else {
                    keySet.add(entry.getKey());
                }
            }
            return new ArrayList<>(keySet);
        } finally {
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public void clear() throws IOException {
        try {
            backEndRWL.writeLock().lock();
            multiFileMap.clear();
            multiFileMap.save();
        } finally {
            backEndRWL.writeLock().unlock();
        }
    }
}
