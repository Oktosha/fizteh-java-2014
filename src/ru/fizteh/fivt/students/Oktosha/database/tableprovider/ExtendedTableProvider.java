package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 * Like casual tableProvider, but returns betterTables
 */
public interface ExtendedTableProvider extends TableProvider, AutoCloseable {

    DroppableStructuredTable getTable(String name);

    DroppableStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException;

}
