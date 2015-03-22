package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 * Like casual tableProvider, but returns betterTables
 */
public interface ExtendedTableProvider extends TableProvider {

    DroppableStructuredTable getTable(String name);

    DroppableStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException;

    void removeTable(String name) throws IOException;

    Storeable deserialize(Table table, String value) throws ParseException;

    String serialize(Table table, Storeable value) throws ColumnFormatException;

    Storeable createFor(Table table);

    Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException;

    List<String> getTableNames();
}
