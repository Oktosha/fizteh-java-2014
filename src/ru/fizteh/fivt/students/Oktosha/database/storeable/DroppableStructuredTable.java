package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;

/**
 * Created by DKolodzey on 11.03.15.
 */
public interface DroppableStructuredTable extends Table {
    void drop() throws IOException;
}
