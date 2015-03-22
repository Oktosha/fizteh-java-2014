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
    private boolean tableIsDropped;
    private ReadWriteLock rwl;

    public StringTableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff.set(new HashMap<>());
        this.tableIsDropped = false;
        this.rwl = new ReentrantReadWriteLock(true);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        rwl.readLock().lock();
        int ret;

        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            ret = diff.get().size();
        } finally {
            rwl.readLock().unlock();
        }

        return ret;
    }

    @Override
    public String getName() {
        rwl.readLock().lock();
        String ret;

        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            ret = multiFileMap.getName();
        } finally {
            rwl.readLock().unlock();
        }

        return ret;
    }

    @Override
    public String get(String key) {
        rwl.readLock().lock();
        String ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (diff.get().containsKey(key)) {
                ret = diff.get().get(key);
            } else {
                ret = multiFileMap.get(key);
            }
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public String put(String key, String value) {
        rwl.readLock().lock();
        String ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            if (key == null || value == null) {
                throw new IllegalArgumentException();
            }
            ret = get(key);
            diff.get().put(key, value);
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public String remove(String key) {
        rwl.readLock().lock();
        String ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            if (key == null) {
                throw new IllegalArgumentException();
            }
            ret = get(key);
            diff.get().put(key, null);
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public int size() {
        rwl.readLock().lock();
        int ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            ret = multiFileMap.size();
            for (Map.Entry<String, String> entry : diff.get().entrySet()) {
                if (entry.getValue() == null && multiFileMap.get(entry.getKey()) != null) {
                    --ret;
                }
                if (entry.getValue() != null && multiFileMap.get(entry.getKey()) == null) {
                    ++ret;
                }
            }
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public int commit() throws IOException {
        rwl.writeLock().lock();
        int ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            ret = getNumberOfUncommittedChanges();
            for (Map.Entry<String, String> entry : diff.get().entrySet()) {
                if (entry.getValue() == null) {
                    multiFileMap.remove(entry.getKey());
                } else {
                    multiFileMap.put(entry.getKey(), entry.getValue());
                }
            }
            multiFileMap.save();
            diff.get().clear();
        } finally {
            rwl.writeLock().unlock();
        }
        return ret;
    }

    @Override
    public int rollback() {
        rwl.readLock().lock();
        int ret;
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            ret = getNumberOfUncommittedChanges();
            diff.get().clear();
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public List<String> list() {
        rwl.readLock().lock();
        ArrayList<String> ret;
        try {
            if (tableIsDropped) {
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
            ret = new ArrayList<>(keySet);
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    @Override
    public void drop() throws IOException {
        rwl.writeLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            multiFileMap.clear();
            multiFileMap.save();
            tableIsDropped = true;
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
