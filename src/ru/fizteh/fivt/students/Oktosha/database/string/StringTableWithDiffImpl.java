package ru.fizteh.fivt.students.Oktosha.database.string;

import ru.fizteh.fivt.students.Oktosha.database.Diff;
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

    private final MultiFileMap multiFileMap;
    private final ThreadLocal<Diff> diff = new ThreadLocal<>();
    private final ReadWriteLock backEndRWL; /* protects reading/writing to files */

    private Map<String, String> getDiff() {
        return diff.get().getMap();
    }

    private ReadWriteLock getDiffRwl() {
        return diff.get().getRwl();
    }

    public StringTableWithDiffImpl(MultiFileMap multiFileMap) {
        this.multiFileMap = multiFileMap;
        this.diff.set(new Diff());
        this.backEndRWL = new ReentrantReadWriteLock(true);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        try {
            getDiffRwl().readLock().lock();
            return getDiff().size();
        } finally {
            getDiffRwl().readLock().unlock();
        }
    }

    @Override
    public String getName() {
        return multiFileMap.getName();
    }

    @Override
    public String get(String key) {
        try {
            backEndRWL.readLock().lock();
            getDiffRwl().readLock().lock();
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (getDiff().containsKey(key)) {
                return getDiff().get(key);
            }
            return multiFileMap.get(key);
        } finally {
            getDiffRwl().readLock().unlock();
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public String put(String key, String value) {
        try {
            backEndRWL.readLock().lock();
            getDiffRwl().writeLock().lock();
            if (key == null || value == null) {
                throw new IllegalArgumentException();
            }
            String ret = get(key);
            getDiff().put(key, value);
            return ret;
        } finally {
            getDiffRwl().writeLock().unlock();
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public String remove(String key) {
        try {
            backEndRWL.readLock().lock();
            getDiffRwl().writeLock().lock();
            if (key == null) {
                throw new IllegalArgumentException();
            }
            String ret = get(key);
            getDiff().put(key, null);
            return ret;
        } finally {
            getDiffRwl().writeLock().unlock();
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public int size() {
        try {
            backEndRWL.readLock().lock();
            getDiffRwl().readLock().lock();
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
            getDiffRwl().readLock().unlock();
            backEndRWL.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        try {
            backEndRWL.writeLock().lock();
            getDiffRwl().writeLock().lock();
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
            getDiffRwl().writeLock().unlock();
            backEndRWL.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        try {
            getDiffRwl().writeLock().lock();
            int ret = getNumberOfUncommittedChanges();
            getDiff().clear();
            return ret;
        } finally {
            getDiffRwl().writeLock().unlock();
        }
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

    @Override
    public void setDiff(Diff diff) {
        this.diff.set(diff);
    }
}
