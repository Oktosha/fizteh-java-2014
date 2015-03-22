package ru.fizteh.fivt.students.Oktosha.database;

import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class DiffManagerImpl implements DiffManager {
    private final ReadWriteLock rwl;  /* protects diff pool */
    private final Map<DiffId, Diff> idToMap;
    private final Map<DiffId, DroppableStructuredTable> idToTable;
    private final IdentityHashMap<DroppableStructuredTable, Set<DiffId>>  tableToId;
    private final Set<DiffId> releasedIds;
    private DiffId maxId;

    public DiffManagerImpl() {
        this.rwl = new ReentrantReadWriteLock(true);
        this.idToMap = new HashMap<>();
        this.idToTable = new HashMap<>();
        this.tableToId = new IdentityHashMap<>();
        this.releasedIds = new LinkedHashSet<>();
        this.maxId = new DiffId(0);
    }

    @Override
    public int freeDiffsForTable(DroppableStructuredTable table) {
        try {
            rwl.writeLock().lock();
            if (!tableToId.containsKey(table)) {
                return 0;
            }
            for (DiffId id : tableToId.get(table)) {
                idToMap.remove(id);
                idToTable.remove(id);
                releasedIds.add(id);
            }
            return tableToId.remove(table).size();
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void freeDiff(DiffId id) {
        try {
            rwl.writeLock().lock();
            tableToId.get(idToTable.get(id)).remove(id);
            idToMap.remove(id);
            idToTable.remove(id);
            releasedIds.add(id);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public DiffId createDiff(DroppableStructuredTable table) throws PoolIsFullException {
        try {
            rwl.writeLock().lock();
            DiffId newId;
            if (!releasedIds.isEmpty()) {
                newId = releasedIds.iterator().next();
                releasedIds.remove(newId);
            } else if (maxId.canBeIncreased()) {
                newId = maxId;
                maxId = maxId.increased();
            } else {
                throw new PoolIsFullException("unable to create new id; "
                                              + "finish some other transactions and try again");
            }
            idToMap.put(newId, new Diff());
            idToTable.put(newId, table);
            if (!tableToId.containsKey(table)) {
                tableToId.put(table, new LinkedHashSet<>());
            }
            tableToId.get(table).add(newId);
            return newId;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public Diff getDiff(DiffId id) {
        if (id == null) {
            throw new IllegalStateException("diffManager is asked for null diff id");
        }
        try {
            rwl.readLock().lock();
            return idToMap.get(id);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public DroppableStructuredTable getTableForDiff(DiffId id) {
        try {
            rwl.readLock().lock();
            return idToTable.get(id);
        } finally {
            rwl.readLock().unlock();
        }
    }
}
