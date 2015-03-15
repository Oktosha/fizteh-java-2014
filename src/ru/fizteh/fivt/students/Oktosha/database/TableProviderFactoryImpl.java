package ru.fizteh.fivt.students.Oktosha.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by DKolodzey on 12.03.15.
 */
public class TableProviderFactoryImpl implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        return new TableProviderImpl(Paths.get(path));
    }
}
