package ru.fizteh.fivt.students.Oktosha.database;

import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class DiffManagerImpl implements DiffManager {
    final ReadWriteLock rwl;  /* protects diff pool */
    final Map<DiffId, Map<String, String>> idToMap;
    final Map<DiffId, DroppableStructuredTable> idToTable;
    final IdentityHashMap<DroppableStructuredTable, Set<DiffId>>  tableToId;
    final Set<DiffId> releasedIds;
    final DiffId maxId;

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
    public void freeDiff(DroppableStructuredTable table, DiffId id) {
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
            DiffId newId = null;
            if (!releasedIds.isEmpty()) {
                newId = releasedIds.iterator().next();
                releasedIds.remove(newId);
            } else if (maxId.canBeIncreased()) {
                newId = maxId;
                maxId.increase();
            } else {
                throw new PoolIsFullException("unable to create new id; "
                                              + "finish some other transactions and try again");
            }
            idToMap.put(newId, new HashMap<>());
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
    public Map<String, String> getDiff(DiffId id) {
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
