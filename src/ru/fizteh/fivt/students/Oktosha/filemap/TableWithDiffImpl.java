package ru.fizteh.fivt.students.Oktosha.filemap;

import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 04.03.15.
 * Implementation if TableWithDiff (advanced Table)
 */
public class TableWithDiffImpl implements TableWithDiff {

    private MultiFileMap multiFileMap;
    private final ThreadLocal<Map<String, String>> diff = new ThreadLocal<>();
    private boolean tableIsDropped;
    private ReadWriteLock rwl;

    public TableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff.set(new HashMap<>());
        this.tableIsDropped = false;
        this.rwl = new ReentrantReadWriteLock();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        rwl.readLock().lock();
        int ret;

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        ret = diff.get().size();

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public String getName() {
        rwl.readLock().lock();
        String ret;

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        ret = multiFileMap.getName();

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public String get(String key) {
        rwl.readLock().lock();
        String ret;

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

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public String put(String key, String value) {
        rwl.readLock().lock();

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.get().put(key, value);

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public String remove(String key) {
        rwl.readLock().lock();

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String ret = get(key);
        diff.get().put(key, null);

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public int size() {
        rwl.readLock().lock();

        if (tableIsDropped) {
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
        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public int commit()  {
        rwl.writeLock().lock();

        if (tableIsDropped) {
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

        rwl.writeLock().unlock();
        return ret;
    }

    @Override
    public int rollback() {
        rwl.readLock().lock();

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        int ret = getNumberOfUncommittedChanges();
        diff.get().clear();

        rwl.readLock().unlock();
        return ret;
    }

    @Override
    public List<String> list() {
        rwl.readLock().lock();

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

        rwl.readLock().unlock();
        return new ArrayList<>(keySet);
    }

    @Override
    public void drop() throws IOException {
        rwl.writeLock().lock();

        if (tableIsDropped) {
            throw new IllegalStateException();
        }
        multiFileMap.clear();
        multiFileMap.save();
        tableIsDropped = true;

        rwl.writeLock().unlock();
    }
}
