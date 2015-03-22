package ru.fizteh.fivt.students.Oktosha.database;

import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

/**
 * Created by DKolodzey on 22.03.15.
 * thread safe transaction manager
 */
public interface DiffManager {
    int freeDiffsForTable(DroppableStructuredTable table); /* returns number of released diffs */
    void freeDiff(DiffId id);
    DiffId createDiff(DroppableStructuredTable table) throws PoolIsFullException;
    Diff getDiff(DiffId id);
    DroppableStructuredTable getTableForDiff(DiffId id);
}
