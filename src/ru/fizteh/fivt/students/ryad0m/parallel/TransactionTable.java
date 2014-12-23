package ru.fizteh.fivt.students.ryad0m.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionTable implements Table {
    StructedTable structedTable;
    HashMap<String, Storeable> operations = new HashMap<>();
    HashSet<String> deleted = new HashSet<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public TransactionTable(StructedTable structedTable) {
        this.structedTable = structedTable;
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        Set<String> set;
        try {
            set = structedTable.getKeys();
            set.addAll(operations.keySet());
            set.removeAll(deleted);
        } finally {
            lock.readLock().unlock();
        }
        return new ArrayList<String>(set);
    }

    @Override
    public int size() {
        Set<String> set;
        lock.readLock().lock();
        try {
            set = structedTable.getKeys();
            set.addAll(operations.keySet());
            set.removeAll(deleted);
        } finally {
            lock.readLock().unlock();
        }
        return set.size();
    }

    @Override
    public int rollback() {
        int res;
        lock.writeLock().lock();
        try {
            res = operations.size() + deleted.size();
            operations.clear();
            deleted.clear();
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        int res;
        lock.readLock().lock();
        res = operations.size() + deleted.size();
        lock.readLock().unlock();
        return res;
    }

    @Override
    public String getName() {
        return structedTable.getName();
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        Storeable res;
        lock.writeLock().lock();
        try {
            if (key == null || value == null) {
                throw new IllegalArgumentException();
            }
            structedTable.checkIntegrity(value);
            res = get(key);
            deleted.remove(key);
            operations.put(key, ((MyStorable) value).makeCopy());
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }

    @Override
    public Storeable remove(String key) {
        Storeable res;
        lock.writeLock().lock();
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            res = get(key);
            operations.remove(key);
            deleted.add(key);
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }


    @Override
    public int commit() throws IOException {
        int res;
        lock.writeLock().lock();
        try {
            res = operations.size() + deleted.size();
            for (Map.Entry<String, Storeable> operation : operations.entrySet()) {
                structedTable.put(operation.getKey(), operation.getValue());
            }
            deleted.forEach(structedTable::remove);
            operations.clear();
            deleted.clear();
            structedTable.save();
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }


    @Override
    public int getColumnsCount() {
        int res;
        lock.readLock().lock();
        try {
            res = structedTable.getColumnTypes().size();
        } finally {
            lock.readLock().unlock();
        }
        return res;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        Class<?> res;
        lock.readLock().lock();
        try {
            if (columnIndex >= structedTable.getColumnTypes().size()) {
                throw new IndexOutOfBoundsException();
            }
            res = structedTable.getColumnTypes().get(columnIndex);
        } finally {
            lock.readLock().unlock();
        }
        return res;
    }

    public StructedTable getStructedTable() {
        return structedTable;
    }

    @Override
    public Storeable get(String key) {
        Storeable res;
        lock.readLock().lock();
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (deleted.contains(key)) {
                res = null;
            } else if (operations.containsKey(key)) {
                res = operations.get(key);
            } else {
                try {
                    res = structedTable.get(key);
                } catch (ParseException ex) {
                    throw new RuntimeException();
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return res;
    }
}
