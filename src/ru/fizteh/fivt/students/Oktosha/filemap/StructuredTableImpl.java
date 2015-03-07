package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 07.03.15.
 */
public class StructuredTableImpl implements Table {
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable remove(String key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<String> list() {
        return null;
    }

    @Override
    public int commit() throws IOException {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Storeable get(String key) {
        return null;
    }
}
